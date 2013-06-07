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
package com.siemens.scr.avt.ad.annotation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.dcm4che2.data.DicomObject;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.impl.DefaultADFacadeImpl;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.io.DicomIO;
import com.siemens.scr.avt.ad.util.DicomParser;
import com.siemens.scr.avt.ad.util.ResourceLocator;

/**
 * Test for header parsing and round-trip property.
 * 
 * @author Xiang Li
 *
 */

@RunWith(Parameterized.class)
public class AnnotationTest {
	private String dicomFile;
	private File aimFile;
	private File attachmentFile;// In principle it can be a set/list
	
	// just for convenience
	private DicomObject dob;
	private ImageAnnotation annotation;
	private byte[] attachment;
	
	private ADFacade adFacade;
	
	public AnnotationTest(String dicom, String aim, String attachment) throws FileNotFoundException{
		dicomFile = dicom;
		aimFile = ResourceLocator.createFile(this.getClass(), aim);
		attachmentFile = ResourceLocator.createFile(this.getClass(), attachment);
	}
	/*
	@Parameters
	 public static Collection<?> modules() {
	  return Arrays.asList(new Object[][] {
			  {"/RECIST_Data_rv_12/1.3.6.1.4.1.9328.50.1.10717.dcm",
				"/RECIST_Data_rv_12/0022BaselineA.xml",
				"/AVT-AD.pdf",
			  }
			  
	   });
	 }
	*/
	
	
	@Parameters
	 public static Collection<?> modules() {
	  return Arrays.asList(new Object[][] {
			  {"/RECIST_Data_rv_12/1.3.6.1.4.1.9328.50.1.10717.dcm",
				"/test_aim.xml",
				"/test_aim.dcm",
			  }
			  
	   });
	 }
	
	
	
	
	
	@Before
	public  void setup() throws IOException, JDOMException{
		dob = DicomParser.read(this.getClass(), dicomFile);
		
		annotation = AnnotationIO.loadAnnotationWithAttachment(aimFile, attachmentFile);
	}
	
	@Test
	public void testSave() throws JDOMException, IOException{
		
		DicomIO.saveOrUpdateDicom(dob);

		AnnotationIO.saveOrUpdateAnnotation(annotation);
	}
	
	@Test
	public void testGetAnnotation()throws JDOMException, IOException {
		ADFacade adFacade;
		adFacade = new DefaultADFacadeImpl();
		ImageAnnotation imageAnnotation = adFacade.getAnnotation("1.3.6.1.4.1.5962.99.1.2241079968.200827889.1239191693984.2.0");
		//Get attachment
		Set<AnnotationAttachment> annotationAttachments = imageAnnotation.getAttachments();
		for(AnnotationAttachment anot : annotationAttachments) {
			System.out.println(anot.getName());
		}
		
		//Get ReferencedImages
		Set<GeneralImage> images = imageAnnotation.getReferencedImages();
		System.out.println(images);
	}
	
	
	public void testParsing(){
	}
	
	public void testRoundtrip(){
		
	}


}
