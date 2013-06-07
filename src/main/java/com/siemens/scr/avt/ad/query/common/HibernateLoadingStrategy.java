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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.Mapping;
import org.hibernate.type.Type;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

import com.siemens.scr.avt.ad.util.ResourceLocator;

/**
 * A loading strategy that loads information from hibernate mapping files.
 * 
 * @author Xiang Li
 *
 * @param <T> the type of the data structure to be loaded.
 */
public abstract class HibernateLoadingStrategy<T> {
	private static Logger logger = Logger.getLogger(HibernateLoadingStrategy.class);
	
	private Configuration config = new Configuration();
	
	private Mapping mapping;
	
	public void loadFromFile(T t, File file) {
		config.addFile(file);
		load(t);
	}

	protected abstract void load(T t);

	public void loadFromFiles(T t, List<File> files) {
		for (File file : files) {
			config.addFile(file);
		}
		load(t);
	}
	
	public void loadDefault(T t){
		config.configure();
		load(t);
	}
	
	private Mapping getMapping(){
		if(mapping == null){
			mapping = config.buildMapping();
		}
		return mapping;
	}
	
	protected int sqlTypes(Type type){
		return type.sqlTypes(getMapping())[0];
	}
	
	protected Configuration getConfig(){
		return config;
	}
	
	public void loadFromInputStream(T t, InputStream in) {
		config.addInputStream(in);
		load(t);
	}

	public void loadFromInputStreams(T t, List<InputStream> in) {
		for (InputStream ins : in) {
			config.addInputStream(ins);
		}
		
		load(t);
	}
	
	protected static Integer parseHex(String hex){
		final String header = "0x"; 
		hex = hex.toLowerCase();
		if(hex.startsWith(header)){
			hex = hex.substring(hex.indexOf(header) + header.length());
		}
		return Integer.parseInt(hex, 16);
	}
	
	
	protected static String tableNameFromPersistentClass(PersistentClass pc){
		return tableNameFromTable(pc.getTable());
	}
	
	protected static String tableNameFromTable(Table table){
		return table.getSchema() + "." + table.getName();
	}
	
	// for testing
	public static void main(String[] args) throws FileNotFoundException{
		String[] mappings = new String[]{"/dicom.hbm.xml", "/annotation.hbm.xml"};
		LinkedList<File> files = new LinkedList<File>();
		for(String mapping: mappings){
			File file = ResourceLocator.createFile(HibernateLoadingStrategy.class, mapping);
			files.add(file);
		}
		
		NaturalJoinTreeLoadingStrategy strategy = new NaturalJoinTreeLoadingStrategy();
		strategy.loadFromFiles(new NaturalJoinTree(), files);
	}

}
