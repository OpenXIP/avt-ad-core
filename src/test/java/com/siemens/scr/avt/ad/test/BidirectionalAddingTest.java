package com.siemens.scr.avt.ad.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.siemens.scr.avt.ad.annotation.AnnotationAttachment;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.dicom.DicomFactory;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;

public class BidirectionalAddingTest {

	@Test
	public void testPatientStudy(){
		Patient patient = DicomFactory.getInstance().createPatient();
		GeneralStudy study = DicomFactory.getInstance().createStudy();
		
		assertNull(study.getPatient());
		study.setPatient(patient);
		assertEquals(patient, study.getPatient());
		assertFalse(patient.getStudies().contains(study));
		
		study.setPatient(null);
		assertNull(study.getPatient());
		patient.addStudy(study);
		assertTrue(patient.getStudies().contains(study));
		assertEquals(patient, study.getPatient());
	}
	
	@Test
	public void testStudySeries(){
		GeneralStudy study = DicomFactory.getInstance().createStudy();
		GeneralSeries series = DicomFactory.getInstance().createSeries();
		
		assertNull(series.getStudy());
		series.setStudy(study);
		assertEquals(study, series.getStudy());
		assertFalse(study.getSeries().contains(series));
		
		series.setStudy(null);
		assertNull(series.getStudy());
		study.addSeries(series);
		assertTrue(study.getSeries().contains(series));
		assertEquals(study, series.getStudy());
	}
	
	@Test
	public void testSeriesImage(){
		GeneralImage image = DicomFactory.getInstance().createImage();
		GeneralSeries series = DicomFactory.getInstance().createSeries();
		
		assertNull(image.getSeries());
		image.setSeries(series);
		assertEquals(series, image.getSeries());
		assertFalse(series.getImages().contains(image));
		
		image.setSeries(null);
		assertNull(image.getSeries());
		series.addImage(image);
		assertTrue(series.getImages().contains(image));
		assertEquals(series, image.getSeries());
	}
	
	
	@Test
	public void testAnnotationAttachment(){
		ImageAnnotation annotation = new ImageAnnotation();
		AnnotationAttachment attachment = new AnnotationAttachment();
		
		
		assertNull(attachment.getReferencedAnnotation());
		attachment.setReferencedAnnotation(annotation);
		assertEquals(annotation, attachment.getReferencedAnnotation());
		assertFalse(annotation.getAttachments().contains(attachment));
		
		attachment.setReferencedAnnotation(null);
		assertNull(attachment.getReferencedAnnotation());
		annotation.addAttachment(attachment);
		assertTrue(annotation.getAttachments().contains(attachment));
		assertEquals(annotation, attachment.getReferencedAnnotation());
	}
	
	
}
