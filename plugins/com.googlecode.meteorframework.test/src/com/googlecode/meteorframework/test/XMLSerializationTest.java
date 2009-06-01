package com.googlecode.meteorframework.test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.utils.RDFDocumentBuilder;

public class XMLSerializationTest 
extends TestCase 
{
	public void testXMLSerialization() 
	throws Exception 
	{
		Scope repository= Meteor.getSystemScope();
		RDFDocumentBuilder.Constructor builderFactory= repository.getInstance(RDFDocumentBuilder.Constructor.class);
		RDFDocumentBuilder builder= builderFactory.create(DocumentBuilderFactory.newInstance().newDocumentBuilder());
		
		List<Resource> all= repository.getAllResources();
		Collections.sort(all, new Comparator<Resource>() {
			@Override public int compare(Resource o1, Resource o2) {
				return o1.getURI().compareTo(o2.getURI());
			}
		});
		for (Resource resource : all)
			builder.addResource(resource);

		// convert DOM to XML and write to standard out 
		Document document= builder.getDocument();
		TransformerFactory transformerFactory= TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(new DOMSource(document), result);
		System.out.println(result.getWriter().toString());	
	}
}
