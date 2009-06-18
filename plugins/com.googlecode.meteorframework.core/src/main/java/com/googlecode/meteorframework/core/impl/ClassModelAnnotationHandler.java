package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorParseException;
import com.googlecode.meteorframework.core.Namespace;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;


/**
 * Processes @Model annotation attached to Java class declarations.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(ModelElement.class)
@SuppressWarnings("unchecked")
@ModelElement public class ClassModelAnnotationHandler implements ModelAnnotationHandler {
	
	private ArrayList<ModelAnnotationHandler> _handlers= new ArrayList<ModelAnnotationHandler>();
	private Scope _scope;
	private Class _javaType= null;
	private ModelElement _annotation;
	
	public boolean initialize(Scope repository, Object p_annotation, Object p_target) {
		
		_scope= repository;
		
		if (!(p_annotation instanceof ModelElement))
			throw new RuntimeException("This handler only handles Model annotations");
		_annotation= (ModelElement)p_annotation;
		
		if (p_target instanceof Method) {
//			Method method= (Method)p_target;			
//			if (MeteorAnnotationUtils.isMeteorMethod(method)) {
//				if (!Modifier.isAbstract(method.getModifiers()))
//					_handler= new MethodOverloadHandler(true);
//			}
//			else 
//				_handler= new PropertyOverloadHandler(true);
		}
		else if (p_target instanceof Class) {
			_javaType= (Class)p_target;
			
			if (!_javaType.isInterface()) {
				Method[] methods= _javaType.getDeclaredMethods();
				for (Method method : methods) {
					if (!Modifier.isPublic(method.getModifiers()))
						continue;
					ModelAnnotationHandler handler= new MethodModelAnnotationHandler();
					if (handler.initialize(_scope, p_annotation, method))
						_handlers.add(handler);
				}
			}
		}
		
		if (_javaType == null)
			return !_handlers.isEmpty();
		
		SystemScopeBootstrap.registerSystemScopeBinding(_javaType);

		return true;
	}

	public void addBehavior() {
		for (ModelAnnotationHandler handler : _handlers)
			handler.addBehavior();
	}

	public void addDerivedMetadata() {
		for (ModelAnnotationHandler handler : _handlers)
			handler.addDerivedMetadata();
	}

	@Override public void addAnnotationMetadata()
	{
		String turtle= null;
		try {
			if (_javaType != null && 0 < _annotation.value().length()) {
				Type type= DomainImpl.findType(_scope, _javaType);
				turtle= "<"+type.getURI()+"> "+_annotation.value();
				MeteorAnnotationUtils.addMetadata(_scope, turtle);
			}
		} catch (ParseException e) {
			throw new MeteorParseException("Error parsing metadata: "+turtle, e);
		}
	}

	@Override public void addTypeDefinitions()
	{
		if (_javaType != null) {
			// create namespace if needed
			Package pkg= _javaType.getPackage();
			String nsURI= Meteor.getURIForPackage(pkg);
			Namespace ns= RepositoryImpl.findResourceByURI(_scope, nsURI, Namespace.class);
			if (ns == null) {
				ns= _scope.getInstance(Namespace.Constructor.class).create(pkg);
				RepositoryImpl.addResource(_scope, ns);
			}
			
			// create Type
			Type type= TypeAdvice.createType(_scope, _javaType);
			RepositoryImpl.addResource(_scope, type);
		}
	}

}
