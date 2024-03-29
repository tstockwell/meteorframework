package com.googlecode.meteorframework.core.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.TypeLiteral;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;


/**
 * Performs dependency injection.
 *  
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
@Decorator abstract public class ProviderImpl 
implements Provider, Resource
{
	
	static class InstanceEntry {
		BindingContext _bindingContext;
		Object _instance;
		
		@Override public boolean equals(Object obj)
		{
			if (!(obj instanceof InstanceEntry))
				return false;
			InstanceEntry entry= (InstanceEntry) obj;
			if (!entry._instance.equals(_instance)) 
				return false;
			if (!entry._bindingContext.equals(_bindingContext)) 
				return false;
			return true;
		}
	};
	
	
	@Decorates private Provider _self;
	@Inject private Scope _scope;
	
	HashMap<String, List<InstanceEntry>> _instanceEntriesByType= 
		new HashMap<String, List<InstanceEntry>>(); 
	
	/**
	 * Injects dependencies into the fields and methods of an existing object.
	 */ 
	@Override public void injectMembers(Object object, BindingType... bindings) {
		DecoratorManager.injectDependencies(_scope, object, null, BindingContext.getBindingContext(bindings));
	}
	
	@Override public <T> T createInstance(Class<T> javaType, BindingType... bindings)
	{
		if (javaType.isInterface()) 
		{
			InvocationContext ctx= Meteor.getInvocationContext();
			BindingContext bindingContext= ctx.getBindingContext().union(bindings);
			Type type= DomainImpl.findType(_scope, javaType);
			ObjectImpl resourceImpl= new ObjectImpl(_scope, type.getURI(), bindingContext);
			resourceImpl.internalSetURI(Meteor.PROTOCOL+UUID.randomUUID());
			resourceImpl.setValue(CoreNS.Resource.type, type);
			resourceImpl.postConstruct();
	
			return resourceImpl.castTo(javaType);
		}
		
		/*
		 * javaType represents a regular java type, therefore create a regular
		 * object, not a Meteor object that can be decorated.
		 */
		try
		{
			T t= javaType.newInstance();
			return t;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		} 
	}
	
	
	@Override public Object getInstance(Class javaType, BindingType... annotations)
	{
		Object instance= findInstance(javaType, annotations);
		
		if (instance == null) {
			instance= _self.createInstance(javaType, annotations);
			
			/*
			 * If type is singleton then cache the instance 
			 */
			Type type= _scope.findType(javaType);
			if (type != null && type.isSingleton())
				putInstance(instance, javaType, annotations);
		}
		
		return instance;
	}
	
	@Override public <T> T getInstance(TypeLiteral<T> type, BindingType... annotations)
	{
		Class<T> rawType= type.getRawType();
		T instance= findInstance(rawType, annotations);
		if (instance != null)
			return instance;
		return _self.createInstance(rawType, annotations);
	}
	
	@Override public <T> T findInstance(Class<T> javaType, BindingType... annotations)
	{
		InvocationContext ctx= Meteor.getInvocationContext();
		BindingContext bindingContext= ctx.getBindingContext().union(annotations);
		T t= findInstance(javaType, bindingContext);
		if (t != null)
			return t;
		
		List<Provider> nestedScopes= _self.getNestedProviders();
		if (nestedScopes != null) {
			for (Provider nestedScope : nestedScopes) {
				t= nestedScope.findInstance(javaType, annotations);
				if (t != null)
					return t;
			}
		}
		
		return null;
	}
	
	@Override public <T> T findInstance(TypeLiteral<T> javaType, BindingType... annotations)
	{
		return _self.findInstance(javaType.getRawType(), annotations);
	}
	
	protected <T> T findInstance(Class<T> javaType, BindingContext bindingContext) {
		String typeName= javaType.getName();
		List<InstanceEntry> instanceEntries= _instanceEntriesByType.get(javaType.getName());
		if (instanceEntries == null) {
			instanceEntries= new ArrayList<InstanceEntry>();
			_instanceEntriesByType.put(typeName, instanceEntries);
		}
		InstanceEntry bestMatch= null;
		for (InstanceEntry entry : instanceEntries) {
			if (!bindingContext.isSuperSetOf(entry._bindingContext))
				continue;
			if (bestMatch == null) {
				bestMatch= entry;
			}
			else if (entry._bindingContext.isSuperSetOf(bestMatch._bindingContext))
				bestMatch= entry;
		}
		if (bestMatch != null)
			return (T) bestMatch._instance; 
		return null;
	}
	
	@Override public <T> void putInstance(T instance, java.lang.Class<T> javaType, BindingType... annotations) 
	{
		InvocationContext ctx= Meteor.getInvocationContext();
		BindingContext bindingContext= ctx.getBindingContext().union(annotations);
		putInstance(instance, javaType, bindingContext);
	};
	protected void putInstance(Object instance, java.lang.Class<?> javaType, BindingContext bindingContext) 
	{
		String typeName= javaType.getName();
		List<InstanceEntry> instanceEntries= _instanceEntriesByType.get(javaType.getName());
		if (instanceEntries == null) {
			instanceEntries= new ArrayList<InstanceEntry>();
			_instanceEntriesByType.put(typeName, instanceEntries);
		}
		InstanceEntry entry= new InstanceEntry();
		entry._bindingContext= bindingContext;
		entry._instance= instance;
		
		if (!_instanceEntriesByType.containsKey(entry))
			instanceEntries.add(entry);
	};
	
	public <T extends Service> T getInstance(Class<T> serviceClass, BindingType... annotations) {
		InvocationContext ctx= Meteor.getInvocationContext();
		BindingContext bindingContext= ctx.getBindingContext().union(annotations);
		T t= findInstance(serviceClass, annotations);
		if (t == null) {
			t= _self.createInstance(serviceClass, annotations);
			String typeURI= Meteor.getURIForClass(serviceClass);
			ObjectImpl resourceImpl= ObjectImpl.getObjectImpl(t);
			resourceImpl.internalSetURI(typeURI+".instance");
			
			putInstance(t, serviceClass, bindingContext);
		}

		return t;
	}
	
	public Scope getInstance(Class<Scope> javaType) {
		return _scope;
	}
	

	
	public <T extends Annotation> T getInstance(Class<T> annotationClass, BindingType... annotations) {
		return AnnotationProvider.createAnnotation(annotationClass, null);
	}
}
