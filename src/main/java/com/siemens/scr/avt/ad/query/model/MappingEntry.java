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
/**
 * 
 */
package com.siemens.scr.avt.ad.query.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.siemens.scr.avt.ad.query.QueryBuilder;




public abstract class MappingEntry{
	private String tableName;
	private String columnName;
	
	public String toString(){
		return getTableName() + "." + getColumnName();
	}
	
	public MappingEntry(String table, String column){
		tableName = table;
		columnName = column;
	}
	
	/**
	 * The codes are defined by <code>java.sql.Types</code>.
	 * 
	 * @return type of the entry according to <code>java.sql.Types</code>.
	 */
	public abstract int getType();

	public abstract String buildSelectionCondition(QueryBuilder builder, Object value);
	
	public abstract String toProjectionExpression(QueryBuilder builder);
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}
	
	public boolean equals(Object obj){
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
