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
package com.siemens.scr.avt.ad.util;

import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Utility for navigating XML.
 * 
 * @author Xiang Li
 *
 */
public class XMLUtils {
	/**
	 * Retrieving all child nodes with the given path in the given namespace.
	 * 
	 * @param parents a list of parent nodes to start with.
	 * @param path a list of labels representing a path.
	 * @param collector a collecting list.
	 * @param ns a specified namespace.
	 */
	public static void getAllChildrenByPath(List<Element> parents, LinkedList<String> path, List<Element> collector, Namespace ns){
		assert path != null : "path should never be null!";
		
		if(path.size() == 0){
			collector.addAll(parents);
			return;
		}
		
		String step = path.remove(0);

		List<Element> parentCollector = new LinkedList<Element>();
		getAllChildrenByName(parents, step, parentCollector, ns);
		getAllChildrenByPath(parentCollector, path, collector, ns);
	}
	
	private static void getAllChildrenByName(Element parent, String name, List<Element> collector, Namespace ns){
		List<Element> children = parent.getChildren(name, ns);
		collector.addAll(children);
	}
	
	/**
	 * Retrieving all child nodes with the given name in the given namespace.
	 * 
	 * @param parents a list of parent nodes to start with.
	 * @param name the element name.
	 * @param collector a collecting list.
	 * @param ns a specified namespace.
	 */
	public static void getAllChildrenByName(List<Element> parents, String name, List<Element> collector, Namespace ns){
		for(Element parent : parents){
			getAllChildrenByName(parent, name, collector, ns);
		}
	}
	
}
