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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.StringUtils;

/**
 * DICOM related utilities.
 * 
 * @author Xiang Li
 *
 */
public class DicomUtils {
	
	 public static final int ERROR  = -1;
		
	 public static int tagToInt(String tag){
		try {
            int i = (int) Long.parseLong(tag, 16);
            return i;
        } catch (NumberFormatException e) {
        	return ERROR;
        }
	}
	
	 public static String tagToString(int tag){
		StringBuffer buf = new StringBuffer(8);
		StringUtils.shortToHex(tag >> 16, buf);
		StringUtils.shortToHex(tag, buf);
		return buf.toString();
	}
	
	public static InputStream dicomObjectToInputStream(DicomObject dcmObject) throws IOException{
		return dcmToInputStreamByPipe(dcmObject);
	}
	
	public static InputStream dcmToInputStreamByByteBuffer(DicomObject dcmObj) throws IOException{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DicomOutputStream dos = new DicomOutputStream(buffer);
		dos.writeDicomFile(dcmObj);
		ByteArrayInputStream bin = new ByteArrayInputStream(buffer.toByteArray());
		
		return bin;
	}
	
	public static InputStream dcmToInputStreamByPipe(final DicomObject dcmObj) throws IOException{
		PipedInputStream in = new PipedInputStream();
		  final PipedOutputStream out = new PipedOutputStream(in);
		  new Thread(
		    new Runnable(){
		      public void run(){
		    	  try {
		    		DicomOutputStream dos = new DicomOutputStream(out);
					dos.writeDicomFile(dcmObj);
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("Error while converting dicom object to stream!", e);
				}
		      }
		    }
		  ).start();
		  return in;
	}
	
	static InputStream dcmToInputStreamByCircularBuffer(DicomObject dcmObj) {
		// XL: no open source library available, except for a GPL licensed one at ostermiller.org
		return null;
	}
}
