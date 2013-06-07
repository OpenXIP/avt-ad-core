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
package com.siemens.scr.avt.ad.util;

import java.util.Map;

public class Config {
	private static Config config;
	private Map<String, String> env;
	
	public Config(){
		env = System.getenv();  
	}
	
	public String getAdDicomStore(){
		return env.get("AD_DICOM_STORE");
	}
	
	public static Config getConfig(){
		if (config == null)
			config = new Config();
		return config;
	}
}
