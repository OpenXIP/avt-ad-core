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
package com.siemens.scr.avt.ad.query.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jdom.JDOMException;


public interface MappingDictionary {

	public abstract Set<String> keySet();

	public abstract MappingEntry get(String key);

	public abstract void put(String key, MappingEntry entry);

	public abstract void loadFromHibernateMappings(List<File> mappings)
			throws JDOMException, IOException;

	public abstract void loadFromHibernateMapping(File mappingFile)
			throws JDOMException, IOException;

	public abstract void loadFromXPathMapping(File mappingFile);
	
	

}
