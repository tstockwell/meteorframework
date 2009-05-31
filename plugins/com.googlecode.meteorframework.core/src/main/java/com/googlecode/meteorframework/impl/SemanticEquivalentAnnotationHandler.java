package com.googlecode.meteorframework.impl;

import java.lang.reflect.Method;


import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.annotation.ProcessesAnnotations;
import com.googlecode.meteorframework.annotation.SemanticEquivalent;
import com.googlecode.meteorframework.annotation.Setting;
import com.googlecode.meteorframework.utils.Logging;

@ProcessesAnnotations(SemanticEquivalent.class)
@Model 
public class SemanticEquivalentAnnotationHandler implements ModelAnnotationHandler {
	
	public static class SemanticHandler implements ModelAnnotationHandler {
		
		private SemanticEquivalent _semanticEquivalent;
		private Object _annotation;
		private String _targetURI;
		private Scope _scope;
		
		public SemanticHandler(SemanticEquivalent semanticEquivalent) {
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
				Setting[] settings= _semanticEquivalent.value();
				for (Setting setting: settings) 
				{
					String propertyURI= setting.property();
					
					String value= setting.value();
					Object pvalue= value;
					if (value.equals("{$value}") ) {
						Method method= _annotation.getClass().getDeclaredMethod("value", (Class<?>[])null);
						pvalue= method.invoke(_annotation, (Object[])null);
					}
					else if (value.contains("{$value}") ) {
						Method method= _annotation.getClass().getDeclaredMethod("value", (Class<?>[])null);
						Object object= method.invoke(_annotation, (Object[])null);
						pvalue= value.replace("{$value}", object.toString());
					}
						
					resource.setProperty(propertyURI, pvalue);
				}
			} 
			catch (Throwable e)
			{
				e.printStackTrace();
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
		SemanticEquivalent _semanticEquivalent;
		
		public SemanticHandlerFactory(SemanticEquivalent semanticEquivalent)
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
				new SemanticHandlerFactory((SemanticEquivalent)annotation));
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
