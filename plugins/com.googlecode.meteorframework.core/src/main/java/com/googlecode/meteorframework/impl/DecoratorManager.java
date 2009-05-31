package com.googlecode.meteorframework.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import com.googlecode.meteorframework.BindingContext;
import com.googlecode.meteorframework.InvocationContext;
import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.InjectionAnnotationHandler;
import com.googlecode.meteorframework.annotation.InjectionType;


@SuppressWarnings("unchecked")
public class DecoratorManager {
	
	static final Object[] NO_ARGS = new Object[0];
	
	private static WeakHashMap<String, WeakHashMap<Class<?>, List<Field>>> __injectionFields= new WeakHashMap<String, WeakHashMap<Class<?>,List<Field>>>(); 
	private static WeakHashMap<String, WeakHashMap<Field, InjectionAnnotationHandler>> __injectionHandlers= new WeakHashMap<String, WeakHashMap<Field,InjectionAnnotationHandler>>();

	synchronized public static Object getDecorator(InterceptorImpl interceptor, InvocationContext context) {
		ObjectImpl objectImpl= ObjectImpl.getObjectImpl(context.getReceiver());
		Class decoratorClass= interceptor.getHandlerMethod().getDeclaringClass();
		Object decorator= objectImpl.getDecorator(decoratorClass);
		if (decorator == null)
			decorator= objectImpl.createDecorator(decoratorClass);
		return decorator;
	}

	// brain-dead dependency injection.
	// in the future will probably need full-blown dependency manager ala Guice
	public static void injectDependencies(Scope repository, Object decorator, ObjectImpl delegate, BindingContext bindingContext)
	{
		String scopeURI= ObjectImpl.getObjectImpl(repository).internalGetURI();
		WeakHashMap<Class<?>, List<Field>> injectionFieldsByClass= __injectionFields.get(scopeURI);
		if (injectionFieldsByClass == null) {
			injectionFieldsByClass= new WeakHashMap<Class<?>, List<Field>>();
			__injectionFields.put(scopeURI, injectionFieldsByClass);
		}
		WeakHashMap<Field, InjectionAnnotationHandler> injectionHandlersByField= __injectionHandlers.get(scopeURI);
		if (injectionHandlersByField == null) {
			injectionHandlersByField= new WeakHashMap<Field, InjectionAnnotationHandler>();
			__injectionHandlers.put(scopeURI, injectionHandlersByField);
		}
		
		Class decoratorClass= decorator.getClass();
		if (Enhancer.isEnhanced(decoratorClass))
			decoratorClass= decoratorClass.getSuperclass();
		List<Field> injectionFields= injectionFieldsByClass.get(decoratorClass);
		if (injectionFields == null) {
			injectionFields= new ArrayList<Field>();
			for (Field field : decoratorClass.getDeclaredFields()) {
				for (Annotation annotation : field.getAnnotations()) {
					if (annotation.annotationType().isAnnotationPresent(InjectionType.class)) {
						field.setAccessible(true);
						injectionFields.add(field);
						break;
					}
				}
			}
			injectionFieldsByClass.put(decoratorClass, injectionFields);
		}
		
		for (Field field : injectionFields) {
			InjectionAnnotationHandler handler= injectionHandlersByField.get(field);
			if (handler == null) {
				Annotation[] annotations= field.getAnnotations();
				for (Annotation annotation : annotations) {
					List<InjectionAnnotationHandler.Constructor> constructors= Meteor.getInjectionAnnotationHandlers(annotation.annotationType());
					for (InjectionAnnotationHandler.Constructor constructor: constructors) {
						InjectionAnnotationHandler handler2= constructor.createAnnotationHandler();
						if (handler2.initialize(repository, annotation, field, -1)) {
							handler= handler2;
							break;
						}
					}
				}
				if (handler == null) 
					throw new MeteorException("No injection handler available for field "+field.toGenericString());
				injectionHandlersByField.put(field, handler);
			}

			handler.inject(decorator, delegate, bindingContext);
		}
		
	}

}
