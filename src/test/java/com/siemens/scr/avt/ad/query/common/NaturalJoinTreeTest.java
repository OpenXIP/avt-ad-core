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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.siemens.scr.avt.ad.query.common.NaturalFKEdge;
import com.siemens.scr.avt.ad.query.common.NaturalJoinTree;
import com.siemens.scr.avt.ad.query.model.FKEdge;
import com.siemens.scr.avt.ad.query.model.JoinTree;
import com.siemens.scr.avt.ad.query.model.JoinTreeTest;
import com.siemens.scr.avt.ad.query.model.TableVertex;


public class NaturalJoinTreeTest extends JoinTreeTest{
	public NaturalJoinTree joinTree;
	private static String testMapping = "/test.hbm.xml";
	private static String expectedFK = "expectedFK.properties";
	
	@Before
	public void setUp(){
		joinTree = new NaturalJoinTree();
	}
	
	@Test
	public void testFindJoinTree(){
		String patient = "patient";
		String study = "study";
		String series = "series";
		String image = "image";
		String annotation = "annotation";
		String attachment = "attachment";
		FKEdge patientStudy = joinTree.addEdge("patientPK", new String[]{study, patient});
		FKEdge studySeries = joinTree.addEdge("studyPK", new String[]{series, study});
		FKEdge seriesImage = joinTree.addEdge("seriesPK", new String[]{image, series});
		FKEdge seriesAnnotation = joinTree.addEdge("seriesPK", new String[]{annotation, series});
		FKEdge annotationAttachment = joinTree.addEdge("annotationPK", new String[]{attachment, annotation});
		
			
		assertEdgesExpectation(new FKEdge[]{patientStudy}, new String[]{patient, study});
		
		assertEdgesExpectation(new FKEdge[]{studySeries, seriesImage }, new String[]{study, image});
		
		assertEdgesExpectation(new FKEdge[]{seriesAnnotation, studySeries, patientStudy, annotationAttachment, }, new String[]{patient, attachment});
		
	}
	
	
	
	
	
	@Test
	public void testNaturalFKEdge2String(){
		TableVertex from = joinTree.createVertex("study");
		
		TableVertex to = joinTree.createVertex("patient");
		
		String fk = "patient_PK";
		
		NaturalFKEdge edge = new NaturalFKEdge(from, to, fk);
		
		assertEquals("study.patient_PK=patient.patient_PK", edge.toJoin());
	}
	
	@Test
	public void testLoadingFromHibernateMapping() throws JDOMException, IOException, ConfigurationException{
		joinTree.loadFromHibernateMapping(this.getClass().getResourceAsStream(testMapping));
		
		PropertiesConfiguration config = new PropertiesConfiguration(expectedFK);
		HashMap<String, String[]> fks = new HashMap<String, String[]>();
		Iterator it = config.getKeys();
		while(it.hasNext()){
			String key = (String) it.next();
			fks.put(key, config.getStringArray(key));
		}
		
		Set edges = getGraph().getEdges();
		
		assertEquals(fks.keySet().size(), edges.size());
		
		for(Object obj : edges){
			NaturalFKEdge edge = (NaturalFKEdge) obj;
			assertEquals(fks.get(edge.getFK())[0], edge.getEndpoints().getFirst().toString());
			assertEquals(fks.get(edge.getFK())[1], edge.getEndpoints().getSecond().toString());
		}
		
	}
	
	

	@Override
	protected JoinTree getJoinTree() {
		// TODO Auto-generated method stub
		return joinTree;
	}
}
