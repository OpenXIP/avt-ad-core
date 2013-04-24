package com.siemens.scr.avt.ad.query.common;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.siemens.scr.avt.ad.query.common.DicomMappingDictionary;
import com.siemens.scr.avt.ad.query.common.SimpleEntry;
import com.siemens.scr.avt.ad.query.model.MappingEntry;


public class MappingDictionaryTest {
	private static String fileName = "/test.hbm.xml";
	private InputStream mappingFile;
	private String expectedMappingFile = "expectedDict.properties";
	
	@Before
	public void setUp(){
		mappingFile = this.getClass().getResourceAsStream(fileName);
	}
	
	
	@Test
	public void testLoadingFromHibernateMapping() throws JDOMException, IOException, ConfigurationException{
		DicomMappingDictionary dict = new DicomMappingDictionary();
		dict.loadFromHibernateMapping(mappingFile);
		
		PropertiesConfiguration config = new PropertiesConfiguration(expectedMappingFile);
		Map<String, MappingEntry> expected = new HashMap<String, MappingEntry>();
		Iterator it = config.getKeys();
		while(it.hasNext()){
			String key = (String) it.next();
			
			List<String> list = config.getList(key);
			
			int type = Integer.parseInt(list.get(2));
			MappingEntry entry;
			if(type == java.sql.Types.OTHER){
				entry = new WildcardEntry(list.get(0));
			}
			else{
				entry = new SimpleEntry(list.get(0), list.get(1), type);
			}
			expected.put(key, entry);
		}
		Map<String, MappingEntry> actual = dict.getDict();
		
		
		assertEquals(expected.size(), actual.size());
		
		for(String key : expected.keySet()){
			assertEquals(expected.get(key), actual.get(key));	
		}
		
		
	}
	
	public void testLoadingXPathMapping(){
		
	}
	
}
