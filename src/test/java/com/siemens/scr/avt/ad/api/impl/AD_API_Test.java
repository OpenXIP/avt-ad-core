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
package com.siemens.scr.avt.ad.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.FacadeManager;

public class AD_API_Test {
	private ADFacade facade;

	@Before
	public void init() throws ClassNotFoundException{
		Class.forName("com.siemens.scr.avt.ad.api.impl.DefaultADFacadeImpl");
		facade = FacadeManager.getFacade();
	}
	
	@Test
	public void testRetrieveDicomObjs(){
		Map<String, Object> aimCriteria = new HashMap<String, Object>();
		Map<Integer,Object> dicomCriteria = new HashMap<Integer, Object>();
		aimCriteria.put("User.roleInTrial","Norminal GroundTruth");
		List<DicomObject> dcms = facade.retrieveDicomObjs(dicomCriteria, aimCriteria);
		Assert.assertEquals(129, dcms.size());
		
		dicomCriteria.put(Tag.SOPInstanceUID, "1.2.840.113704.1.111.4044.1226687294.21593");
		dcms = facade.retrieveDicomObjs(dicomCriteria, aimCriteria);
		Assert.assertEquals(1, dcms.size());
	}
	
	@Test
	public void testRetrieveSegmentationObjects(){
		List<DicomObject> dcms = facade.retrieveSegmentationObjects("1.3.6.1.4.1.5962.99.1.1772356583.1829344988.1264492774375.3.0");
		Assert.assertEquals(2, dcms.size());
	}
}
