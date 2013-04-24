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
