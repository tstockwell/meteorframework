package com.googlecode.meteorframework.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


import com.googlecode.meteorframework.core.annotation.InjectionAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler.Factory;
import com.googlecode.meteorframework.core.impl.SystemScopeBootstrap;
import com.googlecode.meteorframework.utils.Logging;


public class Meteor { 
	
	public static final String PROTOCOL= "meteor:";
	
	public static final String FACETS_SYSTEM_PROPERTY= "com.googlecode.meteorframework.facets";
	
	static HashMap<Class<?>, List<ModelAnnotationHandler.Factory>> _annotationHandlers= new HashMap<Class<?>, List<ModelAnnotationHandler.Factory>>();
	static HashMap<Class<?>, List<InjectionAnnotationHandler.Constructor>> _injectionHandlers= new HashMap<Class<?>, List<InjectionAnnotationHandler.Constructor>>();
	private static final ThreadLocal<Stack<InvocationContext>> _invocationContext= new ThreadLocal<Stack<InvocationContext>>();
	
	/**
	 * @return the system repository
	 */
	public final static Scope getSystemScope() {
		return SystemScopeBootstrap.getSystemScope();
	}


	public static final void popInvocationContext() {
		Stack<InvocationContext> stack= _invocationContext.get();
		if (stack == null) {
			stack= new Stack<InvocationContext>();
			_invocationContext.set(stack);
		}
		stack.pop();
	}

	public static final InvocationContext getInvocationContext() {
		try {
			Stack<InvocationContext> stack= _invocationContext.get();
			return stack.lastElement();
		}
		catch (Throwable t) {
			throw new MeteorException("No invocation context available.  This method may only be called from within a Meteor-managed method call.");
		}
	}

	public static final void pushInvocationContext(InvocationContext context) {
		Stack<InvocationContext> stack= _invocationContext.get();
		if (stack == null) {
			stack= new Stack<InvocationContext>();
			_invocationContext.set(stack);
		}
		stack.push(context);
	}
	
	public static final boolean isMeteorInvocation() {
		Stack<InvocationContext> stack= _invocationContext.get();
		return stack != null && !stack.isEmpty();
	}
	
	
	public static void registerModelAnnotationHandler(Class<?> annotationType, ModelAnnotationHandler.Factory handlerFactory) {
		List<ModelAnnotationHandler.Factory> list= _annotationHandlers.get(annotationType);
		if (list == null) {
			list= new ArrayList<ModelAnnotationHandler.Factory>();
			_annotationHandlers.put(annotationType, list);			
		}
		list.add(handlerFactory);
	}
	
	@SuppressWarnings("unchecked")
	public static List<ModelAnnotationHandler.Factory> getModelAnnotationHandlers(Class<?> annotationType) {
		List<ModelAnnotationHandler.Factory> list= _annotationHandlers.get(annotationType);
		if (list == null)
			return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(list);
	}

	public static Map<Class<?>, List<Factory>> getAllModelAnnotationHandlers()
	{
		return Collections.unmodifiableMap(_annotationHandlers);
	}
	
	public static <T extends ModelAnnotationHandler> void removeModelAnnotationHandler(Class<?> annotationType, Class<T> handlerType) {
		List<ModelAnnotationHandler.Factory> list= _annotationHandlers.get(annotationType);
		if (list == null)
			return;
		list.remove(handlerType);
	}

	
	
	public static void registerInjectionAnnotationHandler(Class<?> annotationType, InjectionAnnotationHandler.Constructor handlerFactory) {
		List<InjectionAnnotationHandler.Constructor> list= _injectionHandlers.get(annotationType);
		if (list == null) {
			list= new ArrayList<InjectionAnnotationHandler.Constructor>();
			_injectionHandlers.put(annotationType, list);			
		}
		list.add(handlerFactory);
	}
	
	@SuppressWarnings("unchecked")
	public static List<InjectionAnnotationHandler.Constructor> getInjectionAnnotationHandlers(Class<?> annotationType) {
		List<InjectionAnnotationHandler.Constructor> list= _injectionHandlers.get(annotationType);
		if (list == null)
			return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(list);
	}

	public static Map<Class<?>, List<InjectionAnnotationHandler.Constructor>> getAllInjectionAnnotationHandlers()
	{
		return Collections.unmodifiableMap(_injectionHandlers);
	}
	
	public static <T extends ModelAnnotationHandler> void removeInjectionAnnotationHandler(Class<?> annotationType, Class<T> handlerType) {
		List<InjectionAnnotationHandler.Constructor> list= _injectionHandlers.get(annotationType);
		if (list == null)
			return;
		list.remove(handlerType);
	}
	
	
	
	public static String getURIForClass(Class<?> javaClass) {
		return PROTOCOL+javaClass.getName();
	}


	public static String getURIForMethod(Method method) {
		String uri= PROTOCOL+method.getDeclaringClass().getName()+".";
		if (MeteorAnnotationUtils.isMeteorProperty(method)) {
			String fieldName= getPropertyName(method.getName());
			if (fieldName.length() <= 0) 
				Logging.warning("Cannot determine field name for a Meteor property method:"+method.toGenericString());
			uri+= fieldName;
		}
		
		if (uri.endsWith("."))
			uri+= method.getName();
		
		return uri;		
	}

	/**
	 * Return the property name for a given method name. 
	 */
	public static final String getPropertyName(String methodName) {
		String fieldName= methodName;
		if (fieldName.startsWith("get") || fieldName.startsWith("set")) {
			fieldName= fieldName.substring(3);
		}
		else if (fieldName.startsWith("is")) 
			fieldName= fieldName.substring(2);
		if (0 < fieldName.length()) 
			fieldName= fieldName.substring(0,1).toLowerCase()+fieldName.substring(1);
		return fieldName;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T proceed() {
		return (T)getInvocationContext().proceed();
	}

	public static String getURIForPackage(Package pkg) {
		return PROTOCOL+pkg.getName();
	}

	public static String getURIForJavaElement(Object target) {
		if (target == null)
			return null;
		if (target instanceof Class) 
			return getURIForClass((Class<?>)target);
		if (target instanceof Method) {
			return getURIForMethod((Method)target);
		}
		throw new MeteorException("Cannot create URI for Java element of type "+target.getClass().getName());
	}

	public static final Object getResult()
	{
		return getInvocationContext().getResult();
	}
}
