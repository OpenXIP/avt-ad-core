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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;


import com.siemens.scr.avt.ad.annotation.AnnotationAttachment;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.util.DicomParser;
import com.siemens.scr.avt.ad.util.ResourceLocator;

/**
 * All files in the directory with suffix ".xml" should be valid AIM anntoations of v1 revision 12.
 * 
 * @author Xiang Li
 *
 */
public class AnnotationBatchLoader extends BatchLoader<ImageAnnotation> {
	private static Logger logger = Logger.getLogger(AnnotationBatchLoader.class);
	
	
	
	private List<String> segmentAttachmentIds = new ArrayList<String>();
	private HashMap<String, File> sopInstanceUID2URL = new HashMap<String, File>();
	
	public static void main(String[] args){
	    initLogging();
		assert args.length > 0 : "A directory should be provided!";
		
		String path = args[0];
		logger.debug("Loading from directory " + path);
		
		File directory = new File(path);
		
		assert directory != null : "Directory should exist!"; 
		assert directory.isDirectory() : "The path " + path + " should point to a directory!";
		
		List<String> argStrings = new ArrayList<String>();
		for(int i = 0; i < args.length; i++) {
			argStrings.add(args[i]);
		}		
		
		AnnotationBatchLoader loader = new AnnotationBatchLoader();
		try {
			loader.parseSegDicomFromDirectroy(directory);
			loader.loadFromDirectory(directory);
		} catch (Exception e) {
			logger.error("Error while loading:");
			e.printStackTrace();
		}
		logger.info("done");
	}
	
	@Override
	protected String getSuffix() {
		return ".xml";
	}

	@Override
	protected void loadSingleObject(ImageAnnotation t) {
		AnnotationIO.saveOrUpdateAnnotation(t);		
	}

	@Override
	protected void preprocessSingleObject(ImageAnnotation t) {
		// do nothing
	}

	@Override
	protected ImageAnnotation readFromFile(File file) throws Exception{
		
		
		this.segmentAttachmentIds = getSegmentAttachmentIds(file);
		
		ImageAnnotation annotation = AnnotationIO.loadAnnotationFromFile(file);
		
		for (String uid : segmentAttachmentIds){
			File attachmentFile = sopInstanceUID2URL.get(uid);
			if(attachmentFile == null) continue;
			AnnotationAttachment attachment = AnnotationIO.getFactory().createAttachmentFromFile(attachmentFile);
			//annotation.addAttachment(attachment);
		}
		return annotation;
	}
	
	@Override
	protected void preprocess(Iterator<File> fileIterator) throws Exception{
		// do nothing
	}
	
	protected List<String> getSegmentAttachmentIds(File aimFile) throws IOException, JDOMException {
		List<String> segmentAttachmentIds = new ArrayList<String>();
		FileInputStream fin = new FileInputStream(aimFile);
		String aimXML = ResourceLocator.getStringFromStream(fin);
		
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		StringReader reader = new StringReader(aimXML);
		Document doc;
		try {
			doc = builder.build(reader);			
		} finally {
			reader.close();
		}
		Element root = doc.getRootElement();
		Element probabilityMapCollection = root.getChild("probabilityMapCollection", Namespace.getNamespace("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM"));
		if (probabilityMapCollection != null) {
			List<Element> ProbabilityMaps = new ArrayList<Element>();		
			List<Element> tempProbabilityMaps = probabilityMapCollection.getChildren("ProbabilityMap", Namespace.getNamespace("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM"));
			ProbabilityMaps.addAll(tempProbabilityMaps);
			for(Element element : ProbabilityMaps) {
				String refInstanceUID = element.getAttributeValue("referencedInstanceUID");
				segmentAttachmentIds.add(refInstanceUID);
			}
		}
		fin.close();
		return segmentAttachmentIds;	
		
	}
	
	private void parseSegDicomFromDirectroy(File directory) throws IOException {
		Iterator<File> fileIterator = FileUtils.iterateFiles(directory, new SuffixFileFilter(".dcm"), TrueFileFilter.INSTANCE);
		while(fileIterator.hasNext()){
			File file = fileIterator.next();			
			DicomObject dob = DicomParser.read(file);
			if(dob == null) continue;
			String dicomUID = dob.getString(Tag.MediaStorageSOPInstanceUID);
			sopInstanceUID2URL.put(dicomUID, file);			
		}
	}
	
	
	
	

	
	

}
