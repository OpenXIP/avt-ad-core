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
package com.siemens.scr.avt.ad.util;

import java.io.IOException;
import java.io.InputStream;

import org.dcm4che2.data.DicomObject;

import edu.wustl.xipHost.wg23.NativeModelRunner;

/**
 * Yet another XML format for the header.
 * 
 * @author Xiang Li
 *
 */
public class DicomHeader2XML_WU {
	/**
	 * 
	 * @param dcmObj a DICOM object
	 * @return a string representation of the header in XML, null if error occurs.
	 */
	public String convertToXML(DicomObject dcmObj){
		
		String xml = null;
		try {
			InputStream in;
			in = DicomUtils.dicomObjectToInputStream(dcmObj);
			NativeModelRunner runner = new NativeModelRunner(in);
			xml = runner.makeXMLNativeModel();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return xml;
	}
}
