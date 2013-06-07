/*
Copyright (c) 2010, Siemens Corporate Research a Division of Siemens Corporation 
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.siemens.scr.avt.ad.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.iod.module.composite.GeneralSeriesModule;
import org.dcm4che2.iod.module.composite.GeneralStudyModule;
import org.dcm4che2.iod.module.composite.PatientModule;
import org.dcm4che2.iod.module.general.SOPCommonModule;
import org.hibernate.Session;

import com.siemens.scr.avt.ad.dicom.DicomFactory;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.Config;
import com.siemens.scr.avt.ad.util.DicomParser;
import com.siemens.scr.avt.ad.util.HibernateUtil;

public class DicomIO {
	private static Logger logger = Logger.getLogger(DicomIO.class);
	
	private static final byte NONE = 0x00;
	private static final byte IMAGE = 0x01;
	private static final byte SERIES = 0x02;
	private static final byte STUDY = 0x04;
	private static final byte PATIENT = 0x08;
	
	public static int saveOrUpdateDicomFromFile(Class<?> clazz, String filePath){
		try {
			return saveOrUpdateDicomFromStream(new FileInputStream(Config.getConfig().getAdDicomStore() + File.separator + filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int saveOrUpdateDicomFromStream(InputStream in){
		DicomObject dicomObj = DicomParser.read(in);
		return saveOrUpdateDicom(dicomObj);
	}
	
	/**
	 * Saves a DICOM object with duplication detection.  
	 * 
	 * 
	 * @param dicomObj a DICOM object
	 * @return the PK_ID of the saved entity. Depending on the update level,
	 *  it can be PK_ID of a patient, a study, a series or an image. 
	 */
	public static int saveOrUpdateDicom(DicomObject dicomObj){
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		Object[] updateParams = testForUpdateLevel(dicomObj, session); 
		Byte updateLevel = (Byte) updateParams[0];
		Object parent = updateParams[1];
		
		
		
		int id = updateAtLevelSavingOnParent(updateLevel, parent, dicomObj, session);
		
		session.close();
		
		return id;
	}
	
	public static boolean removeDicom(String SOPInstanceUID){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralImage image = Queries.findImage(SOPInstanceUID, session);
		if(image != null){
			session.delete(image);
		}
		session.close();
		return false;
	}
	

	/**
	 * Retrieve a DICOM object with the given SOPInstanceUID including pixel data.
	 * 
	 * @param SOPInstanceUID a UID uniquely identifying a SOPInstance.
	 * @return a DICOM object representing the SOPInstance including pixel data.
	 */
	public static DicomObject dumpDicom(String SOPInstanceUID){
		return dumpDicom(SOPInstanceUID, true);
	}
	
	/**
	 * Retrieve a DICOM object with the given SOPInstanceUID from the backend DB.
	 * 
	 * @param SOPInstanceUID a UID uniquely identifying a SOPInstance.
	 * @param includingPixel indicating whether the dumped DICOM object should include pixel data.
	 * @return a DICOM object representing the SOPInstance.
	 */
	public static DicomObject dumpDicom(String SOPInstanceUID, boolean includingPixel){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralImage image = Queries.findImage(SOPInstanceUID, session);
		session.close();
		
		if(image == null)
			return null;
		
		DicomInputStream inputStream;
		DicomObject dcmObj = null;
		String path = image.getUri();
		
		if (!StringUtils.isEmpty(path))
		try {
			inputStream = new DicomInputStream(new File(path));
			if (!includingPixel)
				inputStream.setHandler(new StopTagInputHandler(Tag.PixelData));
			dcmObj = inputStream.readDicomObject();
		} catch (IOException e1) {
			logger.error("Cannot load DICOM object from file system!", e1);
			return null;
		}
		return dcmObj;
	}
	
	public static void dumpDicom2File(String SOPInstanceUID, String fileName) throws IOException{
		DicomObject dicomObj = dumpDicom(SOPInstanceUID);
		DicomOutputStream dout = new DicomOutputStream(new FileOutputStream(fileName));
		dout.writeDicomFile(dicomObj);
		dout.close();
	}
	
	
	private static Object[] testForUpdateLevel(DicomObject dcm, Session session){
		Byte updateLevel;
		Object parent = null;
		
		String SOPInstanceUID = new SOPCommonModule(dcm).getSOPInstanceUID();
		String seriesInstanceUID = new GeneralSeriesModule(dcm).getSeriesInstanceUID();
		String studyInstanceUID = new GeneralStudyModule(dcm).getStudyInstanceUID();
		String patientID = new PatientModule(dcm).getPatientID();
		Date patientBirthDate = new PatientModule(dcm).getPatientBirthDate();
		
		
		
		if((parent = Queries.findImage(SOPInstanceUID, session)) != null){
			updateLevel = NONE;
		}
		else if((parent = Queries.findSeries(seriesInstanceUID, session)) != null){
			updateLevel = IMAGE;
		}
		else if((parent = Queries.findStudy(studyInstanceUID, session)) != null){
			updateLevel = SERIES;
		}
		else if((parent = Queries.findPatient(patientID, patientBirthDate, session)) != null){
			updateLevel = STUDY;
		}
		else{
			updateLevel = PATIENT;
		}
		
		
		return new Object[] {updateLevel, parent};
	}
	
	
	private static int updateAtLevelSavingOnParent(byte updateLevel, Object parent, DicomObject dcm, Session session){
		DicomOutputStream output;
		File file = null;
		try {
			file = new File(Config.getConfig().getAdDicomStore() + File.separator + dcm.getString(0x00080018));
			output = new DicomOutputStream(new FileOutputStream(file));
			output.writeDicomFile(dcm);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
		switch(updateLevel){
		case STUDY :
			Patient patient = (Patient) parent;
			GeneralStudy newStudy = DicomFactory.getInstance().createStudy(dcm, file.getAbsolutePath()); 
			patient.addStudy(newStudy);
			HibernateUtil.saveOrUpdate(patient, session);
			return newStudy.getPK_ID();
		case SERIES :
			GeneralStudy study = (GeneralStudy) parent;
			GeneralSeries newSeries = DicomFactory.getInstance().createSeries(dcm, file.getAbsolutePath()); 
			study.addSeries(newSeries);
			HibernateUtil.saveOrUpdate(study, session);
			return newSeries.getPK_ID();
		case IMAGE :
			GeneralSeries series = (GeneralSeries) parent;
			GeneralImage image = DicomFactory.getInstance().createImage(dcm, file.getAbsolutePath());
			series.addImage(image);
			HibernateUtil.saveOrUpdate(series, session);
			return image.getPK_ID();
		default: case PATIENT:
			Patient newPatient = DicomFactory.getInstance().createPatient(dcm, file.getAbsolutePath());
			HibernateUtil.save(newPatient, session);
			return newPatient.getPK_ID();
		case NONE:
			return -1;
		}
		
	}
}
