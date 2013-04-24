package com.siemens.scr.avt.ad.query.common;

public class PathElement {
	private String elementName;
	private String attributeName;
	private Object value;
	
	public PathElement(String elementName, String attributeName, Object value){
		this.elementName = elementName;
		this.attributeName = attributeName;
		this.value = value;
	}
	
	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public boolean isRootElement(){
		return false;
	}
}
