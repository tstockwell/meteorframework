package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.ModelElement;


public class ReflectionUtils
{

	public static Method getOverriddenMethod(Method handlerMethod)
	{
		String targetName= handlerMethod.getName();
		Class<?>[] class1= handlerMethod.getDeclaringClass().getInterfaces();
		for (Class<?> class2 : class1) {
			if (!class2.isAnnotationPresent(ModelElement.class))
				continue;
			
			Method[] methods= class2.getMethods();
			for (Method method : methods) {
				if (!Modifier.isPublic(method.getModifiers()))
					continue;
				if (targetName.equals(method.getName()))
					return method;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Class<?>> getAllTypesImplementedByDecorator(Class javaType) {
		ArrayList<Class<?>> superTypes= new ArrayList<Class<?>>();
		HashSet<Class> completed= new HashSet<Class>();
		ArrayList<Class> todo= new ArrayList<Class>();
		todo.addAll(Arrays.asList(javaType.getInterfaces()));
		while (!todo.isEmpty()) {
			Class superType= todo.remove(0);
			if (completed.contains(superType))
				continue;
			completed.add(superType);
			if (!superType.isAnnotationPresent(ModelElement.class))
				continue;
			superTypes.add(superType);
			todo.addAll(Arrays.asList(superType.getInterfaces()));
		}
		return superTypes;
	}

	/*
	 * Returns the most immediate interface implemented by the 
	 * decorator that implements the decorated element. 
	 */
	public static Class<?> getDecoratedType(Method handlerMethod)
	{
		String handlerName= handlerMethod.getName();
		String handlerProperty= Meteor.getPropertyName(handlerMethod.getName());
		
		List<Class<?>> implementedTypes= getAllTypesImplementedByDecorator(handlerMethod.getDeclaringClass());
		for (Class<?> implementedType : implementedTypes) {
			Method[] methods= implementedType.getMethods();
			for (Method method : methods) {
				
				boolean match= false;
				
				String methodName= method.getName();
				if (handlerName.equals(methodName)) {
					match= true;
				}
				else if (MeteorAnnotationUtils.isMeteorProperty(method)) {
					String methodProperty= Meteor.getPropertyName(methodName);
					if (methodProperty.equals(handlerProperty))
						match= true;
				}
				
				if (match) {
					/*
					 * Since all Meteor interface implicitly inherit from 
					 * Resource then we should never return Resource as the 
					 * decorated type.  However, since interfaces are not required 
					 * to explicitly extend from Resource (but a Decorator must) 
					 * we have to adjust here.
					 * If the decorated type is Resource then return the most 
					 * immediate interface BESIDES Resource.      
					 */
					if (Resource.class.equals(implementedType)) {
						for (Class<?> class1 : implementedTypes) {
							if (!Resource.class.equals(class1))
								return class1;
						}
					}
					
					return implementedType;
				}
			}
		}
		return null;
	}
	

}
