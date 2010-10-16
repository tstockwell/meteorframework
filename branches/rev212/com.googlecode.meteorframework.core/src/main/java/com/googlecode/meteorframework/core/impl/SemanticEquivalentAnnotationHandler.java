package com.googlecode.meteorframework.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.EquivalentMetadata;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;
import com.googlecode.meteorframework.utils.Logging;

@ProcessesAnnotations(EquivalentMetadata.class)
@ModelElement 
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

				// get turtle metadata and replace all variables
				String turtle= "<"+resource.getURI() + "> " + _semanticEquivalent.value();
				turtle= turtle.trim();
				if (!turtle.endsWith("."))
					turtle+= ".";
				for (Method method : ((Annotation)_annotation).annotationType().getDeclaredMethods())
				{
					String value=""; 
					Object object= method.invoke(_annotation, (Object[])null);
					if (object != null)
					{
						if (object instanceof Class)
						{
							value= Meteor.getURIForClass((Class<?>)object);
						}
						else
							value= object.toString();
					}
					String token= "{$"+method.getName()+"}";
					while (turtle.contains(token))
						turtle= turtle.replace(token, value.toString());
				}
				
				MeteorAnnotationUtils.addMetadata(_scope, turtle);
			} 
			catch (Throwable e)
			{
				Logging.warning("Error while adding metadata from Java annotation", e);
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
