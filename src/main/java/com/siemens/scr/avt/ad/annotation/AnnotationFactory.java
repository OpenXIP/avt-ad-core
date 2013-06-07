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
package com.siemens.scr.avt.ad.annotation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.siemens.scr.avt.ad.util.ResourceLocator;
import com.siemens.scr.avt.ad.util.XMLUtils;

/**
 * <code>AnnotationFactory</code> is a static factory provide annotation related utility methods. 
 * 
 * @author Xiang Li
 *
 */
public class AnnotationFactory {
	private static Logger logger = Logger.getLogger(AnnotationFactory.class);
	
	/**
	 * The namespace is dependent on the AIM version 1 revision 12 schema.
	 */
	public static final  Namespace ns = Namespace.getNamespace("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM");
	
	public  static ImageAnnotation createAnnotationFromFile(File aimFile)
			throws IOException, JDOMException {
		FileInputStream fin = new FileInputStream(aimFile);
		try {
			ImageAnnotation annotation = createAnnotationFromStream(fin);
			return annotation;
		} finally {
			fin.close();
		}
	}

	public static  ImageAnnotation createAnnotationFromStream(InputStream in)
			throws JDOMException, IOException {
		ImageAnnotation annotation = new ImageAnnotation();

		String aimXML = ResourceLocator.getStringFromStream(in);
		annotation.setAIM(aimXML);

		ImageAnnotationDescriptor descriptor = parseDescriptorFromString(aimXML);
		annotation.setDescriptor(descriptor);

		return annotation;
	}

	public static AnnotationAttachment createAttachmentFromBytes(byte[] attachmentContent) {
		AnnotationAttachment attachment = new AnnotationAttachment();
		Blob blob = Hibernate.createBlob(attachmentContent);
		attachment.setAttachmentObject(blob);
		return attachment;
	}

	public static AnnotationAttachment createAttachmentFromFile(File attachmentFile)
			throws IOException {
		byte[] attachmentContent = ResourceLocator.getBytesFromFile(attachmentFile);
		AnnotationAttachment attachment = createAttachmentFromBytes(attachmentContent);
		attachment.setName(attachmentFile.getName());
		return attachment;
	}

	
	
	
	public static AnnotationAttachment createAttachmentFromStream(InputStream in)
			throws IOException {
		AnnotationAttachment attachment = new AnnotationAttachment();
		Blob blob = Hibernate.createBlob(in);
		attachment.setAttachmentObject(blob);
		return attachment;
	}
	
	private static List<Element> getAllImageNodes(Element imageReferenceCollection){
		List<Element> imageNodes = new LinkedList<Element>();
		List<Element> refs = imageReferenceCollection.getChildren("ImageReference", ns);
		LinkedList<String> path = new LinkedList<String>(Arrays.asList(new String[]{"study", "Study", "series", "Series", "imageCollection", "Image"}));
		XMLUtils.getAllChildrenByPath(refs, path, imageNodes, ns);
		
		return imageNodes;
	}


	
	public  static String parseAnnotationUID(File aimFile) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);

		Document doc = builder.build(aimFile);

		Element root = doc.getRootElement();
		String uid = (root.getAttributeValue("uid") != null) ? root.getAttributeValue("uid") : root.getAttributeValue("uniqueIdentifier");
		return uid;
	}

	public static  String parseAnnotationUID(InputStream in) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);

		Document doc = builder.build(in);

		Element root = doc.getRootElement();
		String uid = (root.getAttributeValue("uid") != null) ? root.getAttributeValue("uid") : root.getAttributeValue("uniqueIdentifier");
		return uid;
	}

	public static ImageAnnotationDescriptor parseDescriptor(File aimFile)
			throws JDOMException, IOException {
		FileInputStream fin = new FileInputStream(aimFile);
		try {
			return parseDescriptor(fin);
		} finally {
			fin.close();
		}
	}

	public  static ImageAnnotationDescriptor parseDescriptor(InputStream in)
			throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);

		Document doc = builder.build(in);
		return parseDescriptorFromDoc(doc);
	}

	private static ImageAnnotationDescriptor parseDescriptorFromDoc(Document doc) {
		ImageAnnotationDescriptor descriptor = new ImageAnnotationDescriptor();
		Element root = doc.getRootElement();
		descriptor.setID(Integer.parseInt(root.getAttributeValue("id")));
		String uid = (root.getAttributeValue("uid") != null) ? root.getAttributeValue("uid") : root.getAttributeValue("uniqueIdentifier");
		descriptor.setUID(uid);
		descriptor.setAIMVersion(root.getAttributeValue("aimVersion"));
		descriptor.setComment(root.getAttributeValue("comment"));
		try {
			List<Element> children = root.getChildren("user", Namespace.getNamespace("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM"));
			Element user = root.getChild("user", Namespace.getNamespace("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM"));
			Element User = user.getChild("User", Namespace.getNamespace("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM"));
			String roleInTrial = User.getAttributeValue("roleInTrial");
			descriptor.setRoleInTrial(roleInTrial);
			String authorName = User.getAttributeValue("authorName");
			descriptor.setAuthorName(authorName);		
		} catch (NullPointerException e){
			logger.warn("ImageAnnotation with UID "+descriptor.getUID()+" lacks userInRole Attribute!");
		}


		return descriptor;
	}

	public static  ImageAnnotationDescriptor parseDescriptorFromString(String content)
			throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		StringReader reader = new StringReader(content);
		try {
			Document doc = builder.build(reader);
			return parseDescriptorFromDoc(doc);
		} finally {
			reader.close();
		}

	}

	public  static Document parseDoc(File file) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document doc = builder.build(file);
		return doc;
	}

	public static  List<String> parseReferencedImageUIDsFromString(String aim) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		StringReader reader = new StringReader(aim);
		try {
			Document doc = builder.build(reader);
			return parseReferencedSOPInstanceUIDs(doc);
		} finally {
			reader.close();
		}
	}



	public static List<String> parseReferencedSOPInstanceUIDs(Document doc) {
		Element root = doc.getRootElement();
		Element refCollection = root.getChild("imageReferenceCollection", ns);
		List<Element> imageNodes = getAllImageNodes(refCollection);
		List<String> referenced = new LinkedList<String>();
		for (Element imageNode : imageNodes) {
			String uid = imageNode.getAttributeValue("SOPInstanceUID") != null ? imageNode.getAttributeValue("SOPInstanceUID") : 
				imageNode.getAttributeValue("sopInstanceUID");
			logger.debug("Referencing SOPInstance" + uid);
			referenced.add(uid);
		}

		return referenced;
	}
	

}
