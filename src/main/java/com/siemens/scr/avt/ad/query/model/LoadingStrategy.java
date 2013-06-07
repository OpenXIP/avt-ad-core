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
import java.io.InputStream;
import java.util.List;

public interface LoadingStrategy<T>{
	public void loadFromFile(T t, File file);
	public void loadFromFiles(T t, List<File> files);
	public void loadFromInputStream(T t, InputStream in);
	public void loadFromInputStreams(T t, List<InputStream> in);
	
	public void loadDefault(T t);
	
	public static interface JoinTreeLoadingStrategy<T extends JoinTree<?>> extends LoadingStrategy<T>{
	}
	
	public static interface MappingDictionaryLoadingStrategy<M extends MappingDictionary> extends LoadingStrategy<M>{
	}
}

