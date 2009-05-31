package com.googlecode.meteorframework.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.Namespace;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.Type;
import com.googlecode.meteorframework.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.annotation.ProcessesAnnotations;


/**
 * Processes @Model annotation attached to Java class declarations.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(Model.class)
@SuppressWarnings("unchecked")
@Model public class ClassModelAnnotationHandler implements ModelAnnotationHandler {
	
	private ArrayList<ModelAnnotationHandler> _handlers= new ArrayList<ModelAnnotationHandler>();
	private Scope _scope;
	private Class _javaType= null;
	private Model _annotation;
	
	public boolean initialize(Scope repository, Object p_annotation, Object p_target) {
		
		_scope= repository;
		
		if (!(p_annotation instanceof Model))
			throw new RuntimeException("This handler only handles Model annotations");
		_annotation= (Model)p_annotation;
		
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
		if (_javaType != null) {
			Type type= DomainImpl.findType(_scope, _javaType);
			Map<String, List<String>> annotationProperties= MeteorAnnotationUtils.getAllPropertyValues(_annotation);
			for (Object propertyURI : annotationProperties.keySet()) {
				List<String> values= annotationProperties.get(propertyURI);
				for (String value : values)
					type.setProperty((String)propertyURI, value);
			}
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
