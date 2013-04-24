package com.siemens.scr.avt.ad.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * A two pass batch loader loading from a given directory.
 * 
 * <ul>
 * <li>Files are filtered by suffixes.</li>
 * <li>Sub-directories are recursively processed.</li>
 * </ul>
 *  
 * @author Xiang Li
 *
 * @param <T> the object type to be loaded.
 */
public abstract class BatchLoader<T> {

	private static Logger logger = Logger.getLogger(BatchLoader.class);

    protected static void initLogging() {
     try {
        	Properties props = new Properties();
        	props.load(ClassLoader.getSystemResource("log4j.properties").openStream());
        	PropertyConfigurator.configure(props);
        } catch (Exception e) {
        	System.err.println("unable to load log4j.properties");
        }
    }

	public void loadFromDirectory(File directory) throws Exception {
		preprocess(FileUtils.iterateFiles(directory, new SuffixFileFilter(getSuffix()), TrueFileFilter.INSTANCE));
		load(FileUtils.iterateFiles(directory, new SuffixFileFilter(getSuffix()), TrueFileFilter.INSTANCE));
	}

	protected void load(Iterator<File> fileIterator) throws Exception {
		while(fileIterator.hasNext()){
			File file = fileIterator.next();
			logger.info("loading file:" + file.getAbsolutePath());
//			DicomObject dicomObj = DicomParser.read(file);
			T t = readFromFile(file);
			loadSingleObject(t);
//			loadDicomObject(dicomObj);
		}
	}
	
	protected abstract String getSuffix();
	protected abstract T readFromFile(File file) throws Exception;
	protected abstract void loadSingleObject(T t);
	protected abstract void preprocessSingleObject(T t);
	
	protected void preprocess(Iterator<File> fileIterator) throws Exception {
		while(fileIterator.hasNext()){
			T t = readFromFile(fileIterator.next());
			preprocessSingleObject(t);
		}
	}

}