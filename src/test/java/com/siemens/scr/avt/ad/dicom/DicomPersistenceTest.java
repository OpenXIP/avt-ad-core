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
package com.siemens.scr.avt.ad.dicom;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.siemens.scr.avt.ad.io.DicomIO;
import com.siemens.scr.avt.ad.util.DicomComparator;
import com.siemens.scr.avt.ad.util.DicomParser;

/**
 * DICOM roundtrip test.
 * 
 * @author Xiang Li
 *
 */
@RunWith(Parameterized.class)
public class DicomPersistenceTest {
	public static Logger logger = Logger.getLogger(DicomPersistenceTest.class);
	
	private  String file ;

	
	private DicomObject dob;
	

	public DicomPersistenceTest(String sourceFile){
		this.file = sourceFile;
	}

	
	@Parameters
	 public static Collection sourceFiles() {
	  return Arrays.asList(new Object[][] {
			  {"/RECIST_Data_rv_12/1.3.6.1.4.1.9328.50.1.10704.dcm"},
	   });
	 }


	
	@Before
	public  void setup(){
		dob = DicomParser.read(this.getClass(), file);
	}
	

	
	@Test
	public void roundtripTest() throws IOException{
		String SOPInstanceUID = dob.getString(Tag.SOPInstanceUID);
		DicomIO.saveOrUpdateDicom(dob);
		
		// dump a new copy from DB
		DicomObject loadedDcm = DicomIO.dumpDicom(SOPInstanceUID);
		
		// verify contents
		DicomComparator.compare(dob, loadedDcm);
	
	}


	
	
	
}
