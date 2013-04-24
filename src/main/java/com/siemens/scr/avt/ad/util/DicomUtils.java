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
