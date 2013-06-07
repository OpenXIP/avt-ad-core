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
package com.siemens.scr.avt.ad.io;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;

import com.siemens.scr.avt.ad.util.DicomParser;


/**
 * All files in the directory with suffix ".dcm" should be valid DICOM files.
 * 
 * @author Xiang Li
 *
 */
public  class DicomBatchLoader extends BatchLoader<DicomObject> {
	private static Logger logger = Logger.getLogger(DicomBatchLoader.class);
	
	public static void main(String[] args){
	    initLogging();
		assert args.length > 0 : "A directory should be provided!";
		
		String path = args[0];
		logger.debug("Loading from directory " + path);
		
		File directory = new File(path);
		
		assert directory != null : "Directory should exist!"; 
		assert directory.isDirectory() : "The path " + path + " should point to a directory!";
		
		DicomBatchLoader loader = new DicomBatchLoader();
		try {
			loader.loadFromDirectory(directory);
		} catch (Exception e) {
			logger.error("Error while loading:");
			e.printStackTrace();
		}
		logger.info("done");
	}

	@Override
	protected void loadSingleObject(DicomObject t) {
		DicomIO.saveOrUpdateDicom(t);
	}

	@Override
	protected void preprocessSingleObject(DicomObject t) {
		// do nothing
	}
	
	@Override
	protected void preprocess(Iterator<File> fileIterator) throws Exception{
		// do nothing
	}
	@Override
	protected String getSuffix() {
		return "";//".dcm";
	}


	@Override
	protected DicomObject readFromFile(File file) throws IOException {
		return DicomParser.read(file);
	}
	

}
