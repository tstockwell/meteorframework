package com.googlecode.meteorframework.core.utils;

import javax.xml.parsers.DocumentBuilder;


import org.w3c.dom.Document;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Builds RDF/XML Documents from Resource objects. 
 * @author ted stockwell
 */
@ModelElement public interface RDFDocumentBuilder extends Resource {
	
	@ModelElement public interface Constructor extends Service {
		public RDFDocumentBuilder create(DocumentBuilder builder);
	}
	
	public abstract Document getDocument();
	
	public void createRootElement();

	public void addResource(Resource resource);
}
