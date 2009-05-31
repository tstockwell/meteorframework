package com.googlecode.meteorframework.impl;

import java.lang.reflect.Method;

import com.googlecode.meteorframework.BindingContext;
import com.googlecode.meteorframework.BindingType;
import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Bind;
import com.googlecode.meteorframework.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.annotation.ProcessesAnnotations;


/**
 * Processes @Binding annotations.
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
@ProcessesAnnotations(Bind.class)
@Model public class BindingAnnotationHandler implements ModelAnnotationHandler {
	
	private Scope _scope;
	private Object _target;
	private com.googlecode.meteorframework.annotation.Bind _annotation;
	
	public boolean initialize(Scope scope, Object p_annotation, Object p_target) {

		_scope= scope;
		_target= p_target;
		_annotation= (com.googlecode.meteorframework.annotation.Bind) p_annotation;
		
		Class javaType= null;
		if (p_target instanceof Class) {
			javaType= (Class)p_target;
		}
		else if (p_target instanceof Method) {
			javaType= ((Method)p_target).getDeclaringClass();
		}
		return MeteorAnnotationUtils.isModeledObject(javaType);
	}

	@Override public void addAnnotationMetadata()
	{
		BindingType bindingType= _scope.getInstance(_annotation.value());
		String targetURI= Meteor.getURIForJavaElement(_target);
		Resource targetResource= _scope.findResourceByURI(targetURI);
		BindingContext bindingContext= targetResource.getFacets();
		bindingContext= bindingContext.union(bindingType);
		targetResource.setProperty(MeteorNS.Resource.facets, bindingContext);
	}

	@Override public void addBehavior() {
		// do nothing
	}
	
	@Override public void addDerivedMetadata() {
		// do nothing
	}

	@Override public void addTypeDefinitions()
	{
		// do nothing
	}

}
