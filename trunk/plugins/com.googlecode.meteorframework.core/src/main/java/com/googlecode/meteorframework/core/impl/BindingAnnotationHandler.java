package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Bind;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;


/**
 * Processes @Binding annotations.
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
@ProcessesAnnotations(Bind.class)
@Model public class BindingAnnotationHandler implements ModelAnnotationHandler {
	
	private Scope _scope;
	private Object _target;
	private com.googlecode.meteorframework.core.annotation.Bind _annotation;
	
	public boolean initialize(Scope scope, Object p_annotation, Object p_target) {

		_scope= scope;
		_target= p_target;
		_annotation= (com.googlecode.meteorframework.core.annotation.Bind) p_annotation;
		
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
		BindingContext bindingContext= targetResource.getBindingContext();
		bindingContext= bindingContext.union(bindingType);
		targetResource.setProperty(CoreNS.Resource.bindingContext, bindingContext);
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
