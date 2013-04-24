package com.siemens.scr.avt.ad.query.common;


import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.query.common.AbstractQueryBuilder;
import com.siemens.scr.avt.ad.query.db.DB2QueryBuilder;
import com.siemens.scr.avt.ad.util.DicomUtils;

public class QueryTest {
	private static Logger logger = Logger.getLogger(QueryTest.class);
	
	private AbstractQueryBuilder builder;
	
	private Map<String, Object> input;
	private List<String> outputs;
	 
	
	@Before
	public void setUp() throws JDOMException, IOException{
	
		builder = new DB2QueryBuilder();
		
		
		input = new HashMap<String, Object>();
		input.put("com.siemens.scr.avt.ad.dicom.GeneralImage.highBit", 16);
		input.put("com.siemens.scr.avt.ad.dicom.GeneralSeries.seriesInstanceUID", "1.3.6.1.4.1.9328.50.1.8949");
		input.put("com.siemens.scr.avt.ad.annotation.ImageAnnotation.descriptor.imageAnnotationType", "someType");
//		input.put("com.siemens.scr.avt.ad.dicom.Patient.patientID", 0);
		input.put("com.siemens.scr.avt.ad.dicom.GeneralSeries.seriesDateTime", "2000-01-01 00:00:00.0");
		outputs =  new LinkedList<String>();
		outputs.add("com.siemens.scr.avt.ad.annotation.ImageAnnotation.descriptor.UID");
		outputs.add("com.siemens.scr.avt.ad.dicom.GeneralImage.SOPCommand.SOPInstanceUID");
	}
	
	@Test
	public void testDefaultHeaderQuery(){
		input.clear();
		input.put(DicomUtils.tagToString(Tag.Modality), "CT");
		String query = builder.buildQuery(input, outputs);
		logger.debug(query);
	}
	
	@Test
	public void testSQLGen(){
		String query = builder.buildQuery(input, outputs);
		logger.debug(query);
		
	    System.out.println(builder.getModelManager().printToString());
		
	}
	
	@Test
	public void testFindDicomByCriteria(){
		
		final String PATIENT_ID = "1.3.6.1.4.1.9328.50.1.0022";
		HashMap<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
		dicomCriteria.put(Tag.PatientID, PATIENT_ID);
		List<DicomObject> result = Queries.findDicomByCriteria(dicomCriteria, null);
		for(DicomObject d: result){
			logger.debug("Found dicom:" + d);
		}
	}
}
