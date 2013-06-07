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
package com.siemens.scr.avt.ad.query.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

import com.siemens.scr.avt.ad.query.model.LoadingStrategy.JoinTreeLoadingStrategy;

class NaturalJoinTreeLoadingStrategy extends HibernateLoadingStrategy<NaturalJoinTree> implements JoinTreeLoadingStrategy<NaturalJoinTree> {

	@Override
	protected void load(NaturalJoinTree tree) {
		getConfig().buildMappings();
		Set<ForeignKey> fks = new HashSet<ForeignKey>();
		Iterator<Table> it = getConfig().getTableMappings();
		while(it.hasNext()){
			Table t = it.next();
			Iterator<ForeignKey> fkIt = t.getForeignKeyIterator();
			while(fkIt.hasNext()){
				fks.add(fkIt.next());
			}
		}
		processFKs(tree, fks);
	}

	private void processFKs(NaturalJoinTree tree, Set<ForeignKey> fks) {
		for(ForeignKey fk : fks){
			assert fk.getColumnSpan() == 1;// we do not allow composite FK for now
			String columnName = fk.getColumn(0).getName();
			Table contextTable = fk.getTable();
			String table = tableNameFromTable(contextTable);
			String entityName = fk.getReferencedEntityName();
			PersistentClass referencedPC = getConfig().getClassMapping(entityName);
			String referencedTable = tableNameFromPersistentClass(referencedPC);
			tree.handleFK(table, referencedTable, columnName);
		}
		
		
	}

	
	
}
