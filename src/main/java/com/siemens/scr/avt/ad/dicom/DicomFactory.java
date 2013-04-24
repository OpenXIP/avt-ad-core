package com.siemens.scr.avt.ad.dicom;

import org.dcm4che2.data.DicomObject;

/**
 * The singleton factory creates delegates from a DICOM object, usually a parsed 
 * result out of a DICOM file. All subsequent delegates dependent on the
 * delegate to be created are recursively created too.
 * 
 * @author Xiang Li
 *
 */
public class DicomFactory {
	private static DicomFactory instance = new DicomFactory();
	
	private DicomFactory(){
	}
	
	public static DicomFactory getInstance(){
		return instance;
	}
	
	public Patient createPatient(DicomObject dob, String uri){
		Patient patient = new Patient(dob);
		GeneralStudy study = createStudy(dob, uri);
		patient.addStudy(study);
		return patient;
	}
	
	public GeneralStudy createStudy(DicomObject dob, String uri){
		GeneralStudy study = new GeneralStudy(dob);
		GeneralSeries series = createSeries(dob, uri);
		study.addSeries(series);
		return study;
	}
	
	public GeneralSeries createSeries(DicomObject dob, String uri){
		GeneralSeries series = new GeneralSeries(dob);
		GeneralImage image = createImage(dob, uri);
		series.addImage(image);
		return series;
	}
	
	public GeneralImage createImage(DicomObject dob, String uri){
		GeneralImage image = new GeneralImage(dob);
		image.setUri(uri);
		return image;
	}
	
	public Patient createPatient(){
		return new Patient();
	}
	
	public GeneralStudy createStudy(){
		return new GeneralStudy();
	}
	
	public GeneralSeries createSeries(){
		return new GeneralSeries();
	}
	
	public GeneralImage createImage(){
		return new GeneralImage();
	}
}
