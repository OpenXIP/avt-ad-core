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
