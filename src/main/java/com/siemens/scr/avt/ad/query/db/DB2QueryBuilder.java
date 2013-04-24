package com.siemens.scr.avt.ad.query.db;


import org.apache.commons.lang.StringUtils;

import com.siemens.scr.avt.ad.query.common.AbstractQueryBuilder;
import com.siemens.scr.avt.ad.query.common.ModelManager;
import com.siemens.scr.avt.ad.query.common.PathElement;
import com.siemens.scr.avt.ad.query.common.XPathEntry;
import com.siemens.scr.avt.ad.query.common.SimpleEntry;

/**
 * This class encapsulates any DB2 specific query building.
 * 
 * @author Xiang Li
 *
 */
public class DB2QueryBuilder extends AbstractQueryBuilder {
	public DB2QueryBuilder(){
		ModelManager manager = new ModelManager();
		manager.loadDefault(null);
		this.setModelManager(manager);
	}
	
	private static String quoteString(Object value) {
		return "'" + value + "'";
	}
	
	@Override
	public String buildAtomicSelection(Object value, SimpleEntry entry) {
		String valueString = value.toString();
		if(entry.getType() == java.sql.Types.VARCHAR || entry.getType() ==  java.sql.Types.DATE || entry.getType() == java.sql.Types.TIMESTAMP){
			valueString = quoteString(valueString);
		}
		return entry.getTableName() + "." + entry.getColumnName() + "=" + valueString;
	}

	@Override
	public String buildAtomicSelection(Object value, XPathEntry entry) {
		final String ROOT = "_r";
		return "XMLEXISTS("
		+ quoteString(
		 "$" + ROOT + entry.getPath() + buildNodeTest(entry, value)
		)
		+ "passing " + entry.getTableName() + "." + entry.getColumnName() + " as " + quoteValue(ROOT)
		+")";
	}

	private String buildNodeTest(XPathEntry entry, Object value) {
		return"[" + entry.getNodeTestStep() + "=" + quoteValue(value) + "]"; 
	}

	private String quoteValue(Object value) {
		return "\"" + value + "\"";
	}

	@Override
	public String buildAtomicXMLSelection(PathElement element) {
		String selection = "XMLEXISTS('declare default element namespace \"gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM\"; ($aim/";
		if (!element.isRootElement())
			selection += "/";
		selection += element.getElementName();
		if (!StringUtils.isEmpty(element.getAttributeName()))
			selection += "[@" + element.getAttributeName() + "=\"";//User[@roleInTrial="Algorithm"]
		else
			selection += "[text()=";
		selection += element.getValue() + "\"])' passing AD.IMAGE_ANNOTATION.AIM_OBJECT as \"aim\")";
		return selection;
	}

	
}
