package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Namespace;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;


/**
 * Processes @Decorator annotation attached to Java class declarations.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(Decorator.class)
@SuppressWarnings("unchecked")
@Model public class DecoratorAnnotationHandler implements ModelAnnotationHandler {
	
	private Scope _scope;
	private Class _javaType= null;
	
	static class DecoratedElementInfo {
		String elementURI;
		String decoratedTypeURI;
		@Override public int hashCode() {
			return (elementURI+decoratedTypeURI).hashCode();
		}
		@Override public boolean equals(Object obj)
		{
			return hashCode() == obj.hashCode();
		}
	}
	
	public boolean initialize(Scope scope, Object p_annotation, Object p_target) {

		_scope= scope;
		_javaType= (Class)p_target;
		return true;
	}

	@Override public void addBehavior() {
		
		// create a list of all possible methods names that the decorator can 
		// decorate.
		HashMap<String, HashSet<DecoratedElementInfo>> decoratedMethods= new HashMap<String, HashSet<DecoratedElementInfo>>();
		HashMap<String, HashSet<DecoratedElementInfo>> decoratedProperties= new HashMap<String, HashSet<DecoratedElementInfo>>();
		List<Class<?>> decoratedTypes= ReflectionUtils.getAllTypesImplementedByDecorator(_javaType);
		for (Class<?> decoratedType : decoratedTypes) {
			String decoratedTypeURI= Meteor.getURIForClass(decoratedType);
			Method[] methods= decoratedType.getDeclaredMethods();
			for (Method method : methods) {
				String name= method.getName();
				
				HashMap<String, HashSet<DecoratedElementInfo>> decoratedElements= null;
				if (MeteorAnnotationUtils.isMeteorMethod(method)) {
					decoratedElements= decoratedMethods;
				}
				else if (MeteorAnnotationUtils.isMeteorProperty(method)) {
					decoratedElements= decoratedProperties;
					name= Meteor.getPropertyName(name);
				}
				
				if (decoratedElements != null) {
					HashSet<DecoratedElementInfo> elementList= decoratedElements.get(name);
					if (elementList == null) {
						elementList= new HashSet<DecoratedElementInfo>();
						decoratedElements.put(name, elementList);
					}
					DecoratedElementInfo element= new DecoratedElementInfo();
					element.elementURI= Meteor.getURIForMethod(method);
					element.decoratedTypeURI= decoratedTypeURI;
					elementList.add(element);
				}
			}
		}
		
		
		Method[] methods= _javaType.getDeclaredMethods();
		for (Method handlerMethod : methods) {
			int modifiers= handlerMethod.getModifiers();
			if (Modifier.isAbstract(modifiers))
				continue;
			if (Modifier.isStatic(modifiers))
				continue;
			if (!Modifier.isPublic(modifiers))
				continue;
			if (Modifier.isNative(modifiers))
				continue;
			if (handlerMethod.isSynthetic())
				continue;
			
			String name= handlerMethod.getName();
			String propertyName= Meteor.getPropertyName(name);
			
			Class decoratedType= ReflectionUtils.getDecoratedType(handlerMethod);
			if (decoratedType == null)
				continue;
			String decoratedTypeURI= Meteor.getURIForClass(decoratedType);
			
			HashSet<DecoratedElementInfo> methodElements= decoratedMethods.get(name);
			if (methodElements != null) {
				for (DecoratedElementInfo element : methodElements) {
					Interceptor interceptor= new InterceptorImpl(_scope,  handlerMethod, false);
					addMethodInterceptor(interceptor, decoratedTypeURI, element.elementURI);
				}
			}
			
			HashSet<DecoratedElementInfo> propertyElements= decoratedProperties.get(propertyName);
			if (propertyElements != null) {
				for (DecoratedElementInfo element : propertyElements) {
					Interceptor interceptor= new InterceptorImpl(_scope,  handlerMethod, false);
					addPropertyInterceptor(interceptor, decoratedTypeURI, element.elementURI );
				}
			}
			
		}
	}

	protected void addPropertyInterceptor(Interceptor interceptor, String decoratedTypeURI, String propertyURI)
	{
		ResourceImpl.addPropertyInterceptor(ObjectImpl.getObjectImpl(_scope).internalGetURI(), interceptor, decoratedTypeURI, propertyURI);
	}
	
	protected void addMethodInterceptor(Interceptor interceptor, String decoratedTypeURI, String methodURI) 
	{
		MethodDispatcher.getMethodSpecializer(decoratedTypeURI, methodURI).addInterceptor(interceptor);
	}
	
	@Override public void addDerivedMetadata() {
		// do nothing
	}

	@Override public void addAnnotationMetadata()
	{
		// do nothing
	}

	@Override public void addTypeDefinitions()
	{
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
