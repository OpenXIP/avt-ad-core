package com.siemens.scr.avt.ad.io;

import java.io.File;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.siemens.scr.avt.ad.annotation.ImageAnnotation;


public class AnnotationWithOutAttachmentBatchLoader extends BatchLoader<ImageAnnotation> {
	private static Logger logger = Logger.getLogger(AnnotationWithOutAttachmentBatchLoader.class);
	
	public static void main(String[] args){
		assert args.length > 0 : "A directory should be provided!";
		
		String path = args[0];
		logger.debug("Loading from directory " + path);
		
		File directory = new File(path);
		
		assert directory != null : "Directory should exist!"; 
		assert directory.isDirectory() : "The path " + path + " should point to a directory!";
		
		AnnotationWithOutAttachmentBatchLoader loader = new AnnotationWithOutAttachmentBatchLoader();
		try {
			loader.loadFromDirectory(directory);
		} catch (Exception e) {
			logger.error("Error while loading:");
			e.printStackTrace();
		}
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
		return AnnotationIO.loadAnnotationFromFile(file);
	}
	
	@Override
	protected void preprocess(Iterator<File> fileIterator) throws Exception{
		// do nothing
	}

}
