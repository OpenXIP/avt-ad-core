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

import com.siemens.scr.avt.ad.query.model.FKEdge;
import com.siemens.scr.avt.ad.query.model.JoinTree;
import com.siemens.scr.avt.ad.query.model.TableVertex;

import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.UserData;

/**
 * Natural FK requires the referencing column and the referenced column have the
 * same name.
 * 
 * @author Xiang Li
 *
 */
class NaturalFKEdge extends UndirectedSparseEdge implements FKEdge {

		public NaturalFKEdge(TableVertex from, TableVertex to, String fk) {// natural join
			super(from, to);
			this.addUserDatum(JoinTree.FK, fk, UserData.CLONE);
		}

		public String getFK() {
			return (String) this.getUserDatum(JoinTree.FK);
		}

		public String toJoin() {
			return getEndpoints().getFirst() + "." + getFK() + "="
					+ getEndpoints().getSecond() + "." + getFK();
		}

}
