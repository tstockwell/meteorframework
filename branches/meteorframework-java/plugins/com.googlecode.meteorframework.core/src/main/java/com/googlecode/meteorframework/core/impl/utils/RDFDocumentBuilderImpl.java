package com.googlecode.meteorframework.core.impl.utils;

import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.utils.UtilsNS;
import com.googlecode.meteorframework.core.utils.ConversionService;
import com.googlecode.meteorframework.core.utils.RDFDocumentBuilder;

/**
 * Builds RDF/XML Documents from Resource objects. 
 * @author ted stockwell
 */
@Decorator public abstract class RDFDocumentBuilderImpl implements RDFDocumentBuilder {
	
	@Decorator public static abstract class Constructor implements RDFDocumentBuilder.Constructor {
		@Inject private Scope _scope;
		
		@Override public RDFDocumentBuilder create(DocumentBuilder builder) {
			Document document= builder.newDocument();
			RDFDocumentBuilder rdfBuilder= _scope.createInstance(RDFDocumentBuilder.class);
			rdfBuilder.setProperty(UtilsNS.RDFDocumentBuilder.document, document);
			rdfBuilder.createRootElement();
			return rdfBuilder;
		}
	}
	
	@Override public void createRootElement() {
		Document document= getDocument();
		if (0 < document.getChildNodes().getLength())
			return;		
		
		Element root= document.createElement("rdf:RDF");
		root.setAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		root.setAttribute("xmlns:meteor", "meteor://system#");
		document.appendChild(root);
	}

	@Override public void addResource(Resource resource) {
		Document document= getDocument();
		Element element= document.createElement("rdf:Description");
		element.setAttribute("rdf:about", resource.getURI());
		document.getChildNodes().item(0).appendChild(element);
		Scope repository= getScope();
		ConversionService conversionService= repository.getInstance(ConversionService.class);
		
		Collection<Property<?>> properties= resource.getContainedProperties();
		for (Property<?> property : properties) {
			if (property.isTemporal())
				continue;
			Element element2= document.createElement(property.getURI());
			element.appendChild(element2);
			
			if (property.isMany()) {
				
			}
			else {
				Object value= resource.getProperty(property);
				if (property.isReference()) {
					element2.setAttribute("rdf:resource", ((Resource)value).getURI() );
				}
				else {
					String literal= conversionService.convert(value, String.class);
					element2.setTextContent(literal);
				}
			}
		}
	}
}
