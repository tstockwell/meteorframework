package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;


import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;
import com.googlecode.meteorframework.core.annotation.EquivalentMetadata;
import com.googlecode.meteorframework.core.annotation.Metadata;
import com.googlecode.meteorframework.core.utils.TurtleReader;
import com.googlecode.meteorframework.utils.Logging;

@ProcessesAnnotations(EquivalentMetadata.class)
@Model 
public class SemanticEquivalentAnnotationHandler implements ModelAnnotationHandler {
	
	public static class SemanticHandler implements ModelAnnotationHandler {
		
		private EquivalentMetadata _semanticEquivalent;
		private Object _annotation;
		private String _targetURI;
		private Scope _scope;
		
		public SemanticHandler(EquivalentMetadata semanticEquivalent) {
			_semanticEquivalent= semanticEquivalent;
		}

		@Override public boolean initialize(Scope repository, Object annotation, Object target)
		{
			_scope= repository;
			_annotation= annotation;
			_targetURI= Meteor.getURIForJavaElement(target);
			
			return true;
		}

		@Override public void addBehavior() {
			// do nothing
		}

		@Override public void addAnnotationMetadata() {
			try
			{
				Resource resource= RepositoryImpl.findResourceByURI(_scope, _targetURI);
				if (resource == null) {
					Logging.severe("Failed to add annotation metadata, resource not found: "+_targetURI);
					return;
				}

				String[]  strings= _semanticEquivalent.value();
				String turtle= _targetURI;
				for (String string : strings)
				{
					turtle+= " ";
					turtle+= string;
				}
				
				TurtleReader reader= new TurtleReader(turtle);
				reader.addMetadataToScope(_scope);
			} 
			catch (Throwable e)
			{
				Logging.warning("Error while adding metadata from Java annoation", e);
			}
		}

		@Override public void addTypeDefinitions() {
			// do nothing
		}

		@Override public void addDerivedMetadata() {
			// do nothing
		}
	}
	
	public static class SemanticHandlerFactory implements ModelAnnotationHandler.Factory 
	{
		EquivalentMetadata _semanticEquivalent;
		
		public SemanticHandlerFactory(EquivalentMetadata semanticEquivalent)
		{
			_semanticEquivalent= semanticEquivalent;
		}
		
		@Override public ModelAnnotationHandler createAnnotationHandler()
		{
			return new SemanticHandler(_semanticEquivalent);
		}
	}
	
	
	
	/**
	 * register a generic annotation handler for annotations annotated with @SemanticEquivalent
	 */
	public boolean initialize(Scope repository, Object annotation, Object target) {
		Class<?> annotationType= (Class<?>) target;
		Meteor.registerModelAnnotationHandler(annotationType, 
				new SemanticHandlerFactory((EquivalentMetadata)annotation));
		return false;
	}

	@Override public void addBehavior() {
		// do nothing
	}

	@Override public void addDerivedMetadata() {
		// do nothing
	}

	@Override public void addAnnotationMetadata() {
		// do nothing
	}

	@Override public void addTypeDefinitions() {
		// do nothing
	}

}
