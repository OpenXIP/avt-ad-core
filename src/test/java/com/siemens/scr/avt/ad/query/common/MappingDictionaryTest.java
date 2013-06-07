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
