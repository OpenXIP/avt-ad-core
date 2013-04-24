package com.siemens.scr.avt.ad.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

/**
 * A parser translating DICOM files into <code>DicomObject</code>
 * 
 * @author Xiang Li
 *
 */
public class DicomParser {
	public static DicomObject read(Class<?> clazz, String fileName){
		return read(clazz.getResourceAsStream(fileName));
	}
	
	public static DicomObject read(File file) throws IOException{
		if(!isDicom(file))
			return null;
		
		DicomObject dcmObj = null;
		DicomInputStream din = null;
		try {
			din = new DicomInputStream(file);
			dcmObj = din.readDicomObject();
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				din.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}		
		return dcmObj;
	}
	
	public static DicomObject read(InputStream input){		
		DicomObject dcmObj;
		DicomInputStream din = null;
		try {
		    din = new DicomInputStream(input);
		    dcmObj = din.readDicomObject();
		}
		catch (IOException e) {
		    e.printStackTrace();
		    return null;
		}
		finally {
		    try {
		        din.close();
		    }
		    catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		
		return dcmObj;
	}	
	
	public static boolean isDicom(File file) {
		if(!file.isFile())
			return false;
		FileInputStream inputStream;
		boolean result = false;
		try {
			inputStream = new FileInputStream(file);
			result = isDicom(inputStream);
			inputStream.close();
		} catch(Exception fnfe) {
			fnfe.printStackTrace();
		} 
		return result;
	}
	
	public static boolean isDicom(InputStream in) {
		//Define a Byte[] containing the header of a dicom File.
		//The first 132 bytes of a file should be compared with this array.
		//If equals, then the file is a dicom file.
		//dicom header
		final byte[] header = new byte[132];
		for(int i=0;i < 128;i++) {
			header[i] = 0;
		}
		header[128] = 68;
		header[129] = 73;
		header[130] = 67;
		header[131] = 77;
		
		byte[] fileBegin = new byte[132];
		try {
			int read = in.read(fileBegin);
			if(read < 132) {
				return false;
			}
		}
		catch(IOException ioe) {
			return false;
		}
		
		for(int i=0;i < 132;i++) {
			if(fileBegin[i] != header[i]) {
				return false;
			}
		}
		return true;
	}
	
	

}
