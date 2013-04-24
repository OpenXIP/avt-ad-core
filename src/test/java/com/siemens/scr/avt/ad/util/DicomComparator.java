package com.siemens.scr.avt.ad.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.ElementDictionary;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.TagUtils;


public class DicomComparator {
	private static Logger logger = Logger.getLogger(DicomComparator.class);

	public static void compare(DicomObject expected, DicomObject actual) {
		Set<Integer> comparedTags = new HashSet<Integer>();
		Iterator<DicomElement> it = actual.iterator();
		while(it.hasNext()){
			DicomElement element = it.next();
			int tag = element.tag();
			if(ElementDictionary.getDictionary().nameOf(tag) == ElementDictionary.getUnkown()){
				logger.debug("Tag "+ TagUtils.toString(tag) + "is unknown!");
				continue;
			}
			
			if(element.getBytes() == null || element.getBytes().length == 0){
				logger.debug("Empty tag in actual dicom:"+ ElementDictionary.getDictionary().nameOf(tag)
				+ " expected value: " + expected.get(tag)		
				);
			}
		
			assertDicomElementEquals("Tag '" + ElementDictionary.getDictionary().nameOf(tag) + "' should be equal to the correspondent in the expected.", expected.get(tag), element);
			comparedTags.add(tag);
			
		}
		
		
		it = expected.iterator();
		while(it.hasNext()){
			DicomElement element = it.next();
			if(comparedTags.contains(new Integer(element.tag()))){
				continue;
			}
			if(ElementDictionary.getDictionary().nameOf(element.tag()) == ElementDictionary.getUnkown()){
				logger.debug("Tag "+ TagUtils.toString(element.tag()) + "is unknown!");
				continue;
			}
			
			logger.debug("Tag " + ElementDictionary.getDictionary().nameOf(element.tag()) + " in the origianl DICOM is not available!");
		}
		
	}
	
	public static void assertDicomElementEquals(String msg, DicomElement expected, DicomElement actual){
		if (expected == actual) {
            return;
        }
		
		if( (expected == null && actual.getBytes().length == 0)
				|| (actual == null && expected.getBytes().length == 0))return;
		
		if(expected == null && actual.vr() == VR.TM){
			// TODO: verify it is actually 0000.00
			return;
		}
		
		if(expected == null || actual == null) {
			logger.error(msg + " expected:" + expected + " actual:" + actual);
			fail(msg);
		}
			
		assertEquals(expected.tag(), actual.tag());

		assertEquals(expected.vr(), actual.vr());
		
		VR vr = expected.vr();
		
		if(vr == VR.TM){
			assertEqualsIgnoringTail(msg, expected, actual);
			return;
		}
		
		assertTrue(msg, isEqualsWithPadding(expected.getBytes(), actual.getBytes(), expected.vr()));
	}
	
	
	
	private static void assertEqualsIgnoringTail(String msg, DicomElement expected, DicomElement actual){
		
		assertEquals(msg, expected.getDate(false), actual.getDate(false));
	}
	
	private static boolean isEqualsWithPadding(byte[] expected, byte[] actual, VR vr){
		if(expected.length != actual.length && (actual.length & 1) != 0){
			actual = Arrays.copyOf(actual, actual.length + 1);
			actual[actual.length - 1] = (byte) vr.padding();
		}
		
		return Arrays.equals(expected, actual);
	}
	
}
