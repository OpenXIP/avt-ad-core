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
package com.siemens.scr.avt.ad.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.annotation.ImageAnnotationDescriptor;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;
import com.siemens.scr.avt.ad.query.common.AbstractQueryBuilder;
import com.siemens.scr.avt.ad.query.db.DB2QueryBuilder;
import com.siemens.scr.avt.ad.util.Config;
import com.siemens.scr.avt.ad.util.HibernateUtil;

/**
 * A static facade for useful queries.
 *  
 * @author Xiang Li
 *
 */
public class Queries {
	private static Logger logger = Logger.getLogger(Queries.class);
	
	public static GeneralImage findImage(String SOPInstanceUID, Session session){
		Criteria criteria = session.createCriteria(GeneralImage.class);
		criteria.add(Restrictions.eq("SOPCommand.SOPInstanceUID", SOPInstanceUID));
		List results = criteria.list();
		return (GeneralImage) (results != null && results.size() > 0 ? results.get(0) : null);
	}
	
	public static GeneralImage findImage(String SOPInstanceUID){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralImage image = findImage(SOPInstanceUID, session); 
		session.close();
		return image;
	}
	
	public static GeneralSeries findSeries(String seriesInstanceUID, Session session){
		Criteria criteria = session.createCriteria(GeneralSeries.class);
		criteria.add(Restrictions.eq("seriesInstanceUID", seriesInstanceUID));
		List results = criteria.list();
		return (GeneralSeries) (results != null && results.size() > 0 ? results.get(0) : null);
	}

	public static GeneralSeries findSeries(String seriesInstanceUID){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralSeries series = findSeries(seriesInstanceUID, session); 
		session.close();
		return series;
	}
	
	public static GeneralStudy findStudy(String studyInstanceUID, Session session){
		Criteria criteria = session.createCriteria(GeneralStudy.class);
		criteria.add(Restrictions.eq("studyInstanceUID", studyInstanceUID));
		List results = criteria.list();
		return (GeneralStudy) (results != null && results.size() > 0 ? results.get(0) : null);
	}
	
	
	
	public static GeneralStudy findStudy(String studyInstanceUID){
		Session session = HibernateUtil.getSessionFactory().openSession();
		GeneralStudy study = findStudy(studyInstanceUID, session); 
		session.close();
		return study;
	}
	
	public static Patient findPatient(String patientID, Date patientBirthDate, Session session){
		Criteria criteria = session.createCriteria(Patient.class);
		if(patientID != null && patientID.length() > 0){
			criteria.add(Restrictions.eq("patientID", patientID));	
		}
		if(patientBirthDate != null){
			criteria.add(Restrictions.eq("patientBirthDate", patientBirthDate));
		}
		List results = criteria.list();
		return (Patient) (results != null && results.size() > 0 ? results.get(0) : null);
	}
	
	public static Patient findPatient(String patientID, Date patientBirthDate){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Patient patient = findPatient(patientID, patientBirthDate, session); 
		session.close();
		return patient;
	}
	
	
	public static ImageAnnotation findAnnotation(String annotationUID, Session session){
		Criteria criteria = session.createCriteria(ImageAnnotation.class);
		criteria.add(Restrictions.eq("descriptor.UID", annotationUID));
		List results = criteria.list();
		return (ImageAnnotation) (results != null && results.size() > 0 ? results.get(0) : null);
	}
	
	public static ImageAnnotation findAnnotation(String annotationUID){
		Session session = HibernateUtil.currentSession();
		ImageAnnotation annotation = findAnnotation(annotationUID, session); 
		return annotation;
	}
	
	public static List<ImageAnnotationDescriptor> findAnnotationDescriptorsInSeries(String seriesInstanceUID, Session session){
		Criteria criteria = session.createCriteria(ImageAnnotationDescriptor.class);
		criteria.add(Restrictions.eq("seriesInstanceUID", seriesInstanceUID));
		List<ImageAnnotationDescriptor> results = criteria.list();
		return results;
	}
	

	
	public static List<DicomObject> findDicomByCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria){
		return findDicomByCriteria(dicomCriteria, annotationCriteria, true);
	}
	
	/**
	 * Find all the DICOM object in the database satisfying given criteria.
	 * 
	 * @param dicomCriteria a map from DICOM tag to java.lang.Object representing a conjunctive query.
	 * @param annotationCriteria a map from predefined keys to java.lang.Object representing a conjunctive query.
	 * @param includingPixel indicating whether to include pixel data.
	 * @return a list of DicomObject satisfying given criteria.
	 */
	@SuppressWarnings("unchecked")
	public static List<DicomObject> findDicomByCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria, boolean includingPixel){
		AbstractQueryBuilder builder = new DB2QueryBuilder();
		
		LinkedList<String> output = new LinkedList<String>();
		output.add(Patient.class.getCanonicalName());
		output.add(GeneralStudy.class.getCanonicalName());
		output.add(GeneralSeries.class.getCanonicalName());
		output.add(GeneralImage.class.getCanonicalName());
		String queryString = builder.buildQuery(dicomCriteria, annotationCriteria, output);
		
		logger.debug("Query:" + queryString);
		
		Session session = HibernateUtil.getSessionFactory().openSession();

		SQLQuery query = session.createSQLQuery(queryString);
		query.addEntity(Patient.class)
		.addEntity(GeneralStudy.class)
		.addEntity(GeneralSeries.class)
		.addEntity(GeneralImage.class);
		
		List<Object[]> queryResult = query.list();
		
		LinkedList<DicomObject> result = new LinkedList<DicomObject>();
		
		for(Object[] tuple : queryResult){
			BasicDicomObject dcmObj = new BasicDicomObject();
			Patient patient = (Patient) tuple[0];
			GeneralStudy study = (GeneralStudy) tuple[1];
			GeneralSeries series = (GeneralSeries) tuple[2];
			GeneralImage image = (GeneralImage) tuple[3];
			image.getDicomObject().copyTo(dcmObj);
			series.getDicomObject().copyTo(dcmObj);
			study.getDicomObject().copyTo(dcmObj);
			patient.getDicomObject().copyTo(dcmObj);
			if(includingPixel){
				File file = new File(Config.getConfig().getAdDicomStore() + File.separator + dcmObj.getString(0x00080018));
				try {
					FileInputStream input = new FileInputStream(file);
					DicomInputStream dcmInput = new DicomInputStream(input);
					DicomObject dcm = dcmInput.readDicomObject();
					result.add(dcm);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				result.add(dcmObj);
		}
		
		session.close();
		
		return result;
	}

	/**
	 * Retrieving a list of entities satisfying the given criteria.
	 * 
	 * @param <T> The type of the entities.
	 * @param dicomCriteria a map from DICOM tag to java.lang.Object representing a conjunctive query.
	 * @param annotationCriteria a map from predefined keys to java.lang.Object representing a conjunctive query.
	 * @param entityClass the java.lang.Class representation of the entity type.
	 * @return a list of entities.
	 */
	public static<T> List<T> findEntityByCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria, Class<T> entityClass){
		AbstractQueryBuilder builder = new DB2QueryBuilder();
		
		String outputKey = entityClass.getCanonicalName();
		String queryString = builder.buildQuery(dicomCriteria, annotationCriteria, Collections.singletonList(outputKey));
		
		logger.debug("Query:" + queryString);
		
		Session session = HibernateUtil.currentSession();

		SQLQuery query = session.createSQLQuery(queryString);
		query.addEntity(entityClass);
		
		List<Object> queryResult = query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		LinkedList<T> result = new LinkedList<T>();
		
		for(Object entity : queryResult){
			result.add((T)entity);
		}
		return result;
	}
	
	/**
	 * Retrieving a list of a specified column satisfying the given criteria.
	 * 
	 * @param <T> The type of the value.
	 * @param dicomCriteria a map from DICOM tag to java.lang.Object representing a conjunctive query.
	 * @param annotationCriteria a map from predefined keys to java.lang.Object representing a conjunctive query.
	 * @param outputKey a key identifying the column to be retrieved.
	 * @param clazz a <code>java.lang.Class</code> representation of the column type.
	 * @return a list of values.
	 */
	@SuppressWarnings("unchecked")
	public static<T> List<T> findbyCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria, String outputKey, Class<T> clazz){
		AbstractQueryBuilder builder = new DB2QueryBuilder();
		
		String queryString = builder.buildQuery(dicomCriteria, annotationCriteria, Collections.singletonList(outputKey));
		
		logger.debug("Query:" + queryString);
		
		Session session = HibernateUtil.getSessionFactory().openSession();

		SQLQuery query = session.createSQLQuery(queryString);
		
		List<Object> queryResult = query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		LinkedList<T> result = new LinkedList<T>();
		
		for(Object tuple : queryResult){
			result.add((T)tuple);
		}
		
		session.close();
		
		return result;
	}
}
