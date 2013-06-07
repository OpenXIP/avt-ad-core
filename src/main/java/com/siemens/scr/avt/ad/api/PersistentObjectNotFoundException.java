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
package com.siemens.scr.avt.ad.api;


import org.apache.commons.lang.StringUtils;

public class PersistentObjectNotFoundException extends Exception {
	public static final String LIST_DELIMITER = ",";
	
	public PersistentObjectNotFoundException(Class<?> objType, String[] paramTypes, Object[] params ) {
		this(objType + " with parameters <" + StringUtils.join(paramTypes, LIST_DELIMITER) +">=("+ StringUtils.join(params, LIST_DELIMITER) + ") is not found!");
	}

	public PersistentObjectNotFoundException(Class<?> objType, String[] paramTypes, Object[] params, Throwable cause ){
		this(objType + " with parameters <" + StringUtils.join(paramTypes, LIST_DELIMITER) +">=("+ StringUtils.join(params, LIST_DELIMITER) + ") is not found!", cause);
	}
	
	public PersistentObjectNotFoundException(Class<?> objType, String paramType, Object param ) {
		this(objType + " with parameter " + paramType +" = "+ param + " is not found!");
	}
	
	public PersistentObjectNotFoundException(Class<?> objType, String paramType, Object param, Throwable cause ) {
		this(objType + " with parameter " + paramType +" = "+ param + " is not found!", cause);
	}
	
	public PersistentObjectNotFoundException(String message) {
		super(message);
	}

	public PersistentObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	public PersistentObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
