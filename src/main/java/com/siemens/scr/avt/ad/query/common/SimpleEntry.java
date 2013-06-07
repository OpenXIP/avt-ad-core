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


import com.siemens.scr.avt.ad.query.QueryBuilder;
import com.siemens.scr.avt.ad.query.model.MappingEntry;

/**
 * Simple entries are of primitive types.
 * 
 * @author Xiang Li
 *
 */
public class SimpleEntry extends MappingEntry {
	private int type;
	
	public SimpleEntry(String tableName, String columnName, int type){
		super(tableName, columnName);
		this.type = type;
	}

	@Override
	public int getType() {
		return type;
	}


	@Override
	public String buildSelectionCondition(QueryBuilder builder, Object value) {
		return ((AbstractQueryBuilder)builder).buildAtomicSelection(value, this);
	}

	@Override
	public String toProjectionExpression(QueryBuilder builder) {
		return this.getTableName() + "." + this.getColumnName();
	}

}
