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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;

public abstract class JoinTreeTest {

	public static <T> Set<T> array2Set(T[] array){
		List<T> list = Arrays.asList(array);
		Set<T> set = new HashSet<T>();
		set.addAll(list);
		return set;
	}
	
	protected Graph getGraph(){
		return getJoinTree().getGraph();
	}
	
	protected abstract JoinTree getJoinTree();
	
	protected void assertEdgesExpectation(FKEdge[] expected, String[] tableSet) {
		Set<FKEdge> expectedEdges = array2Set(expected);
		Set<String> tables = array2Set(tableSet);
		
		Set<FKEdge> edges = getJoinTree().findJoinTree(tables);
		
		assertEquals(expectedEdges, edges);
	}
	
	@Test
	public void testVertexCaching(){
		String vertexName = "aVertex";
		
		assertNull(getJoinTree().findVertex(vertexName));
		
		TableVertex v1 = getJoinTree().createVertex(vertexName);
		
		assertTrue(getJoinTree().getGraph().getVertices().contains(v1));
		
		TableVertex v2 = getJoinTree().createVertex(vertexName);
		
		assertEquals(v1, v2);
	}
	

}
