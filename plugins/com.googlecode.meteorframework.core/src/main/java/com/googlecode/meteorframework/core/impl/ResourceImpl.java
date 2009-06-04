package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.IsSingleton;
import com.googlecode.meteorframework.core.utils.ConversionService;



@SuppressWarnings("unchecked")
/**
 * Basic implementation of com.googlecode.meteorframework.Resource class.
 * 
 * These methods are implemented in this class instead of in the com.googlecode.meteorframework.* 
 * classes because these methods use code that is particular to the implementation 
 * in this package.  In order to avoid tying the Meteor model API to this particular 
 * implementation this code is kept out of the com.googlecode.meteorframework.* classes.
 * 
 */
@IsSingleton
@Decorator public abstract class ResourceImpl implements Resource
{

	static private ParameterValueSpecializer __getPropertySpecializer= new ParameterValueSpecializer(); 
	static private ParameterValueSpecializer __setPropertySpecializer= new ParameterValueSpecializer(); 
	
	static private InterceptorImpl __getAnyInterceptor;
	static {
		try {
			__getAnyInterceptor= new InterceptorImpl(SystemScopeBootstrap.__systemScope, ResourceImpl.class.getMethod("getAny", new Class[] { Object[].class }), true);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	static private InterceptorImpl __setAnyInterceptor;
	static {
		try {
			__setAnyInterceptor= new InterceptorImpl(SystemScopeBootstrap.__systemScope, ResourceImpl.class.getMethod("setAny", new Class[] { Object.class, Object[].class }), true);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	
	private ObjectImpl _self= ObjectImpl.getObjectImpl(Meteor.getInvocationContext().getReceiver());
	private Scope _scope= _self._scope.internalCast(Scope.class);
	private ConversionService _conversionService= null;
	

	public static void addPropertyInterceptor(String scopeURI, Interceptor interceptor, String typeURI, String propertyURI)
	{
		boolean isSetMethod= Void.TYPE.equals(interceptor.getHandlerMethod().getReturnType());
		
		ParameterValueSpecializer specializer= isSetMethod ? __setPropertySpecializer : __getPropertySpecializer; 
		specializer.addInterceptor(typeURI, propertyURI, interceptor);
	}
	
	@Override public <T> T getProperty(String propertyURI, Object... parameters) {
		InvocationContextImpl ctx= (InvocationContextImpl) Meteor.getInvocationContext();
		Object[] args= ctx.getArguments().toArray();
		
		/*
		 * If the property is denoted as the @SameAs another property then get the other property. 
		 */
		ObjectImpl propertyObject= ObjectImpl.getObjectImpl(RepositoryImpl.findResourceByURI(_scope, propertyURI));
		if (propertyObject != null) {
			Collection<String> sameAs= (Collection<String>) propertyObject.getValue(MeteorNS.Resource.sameAs);
			if (sameAs != null && !sameAs.isEmpty()) {
				String sameAsPropertyURI= sameAs.iterator().next();
				return getProperty(sameAsPropertyURI, parameters);
			}
		}

		/*
		 *	This method works by creating a stack of interceptors and 
		 *	then invoking the first interceptor.
		 */

		/**
		 * First lookup specializers in receiver itself (that is, look for 
		 * decorators added directly to the object, if any.
		 */
		List<Interceptor> interceptors= new ArrayList<Interceptor>();
		List<String> allTypes= _self.getAllMeteorTypeURIs();
		for (Iterator<String> i= allTypes.iterator(); i.hasNext();) 
		{
			String typeURI= i.next();
			ParameterValueSpecializer specializer= _self.findPropertySpecializer(typeURI, propertyURI);
			if (specializer != null)
				interceptors.addAll(specializer.findInterceptors(propertyURI, _self, args));
		}

		interceptors.addAll(__getPropertySpecializer.findInterceptors(propertyURI, _self, args));

		interceptors.add(0, __getAnyInterceptor);

		// get binding context and then remove interceptors that have any 
		// binding not in the current context.
		BindingContext bindingContext= ctx.getFacets();
		ArrayList<Interceptor> boundInterceptors= new ArrayList<Interceptor>();
		for (Interceptor interceptor : interceptors) {
			if (bindingContext.isSuperSetOf(interceptor.getBindingContext()))
				boundInterceptors.add(interceptor);
		}
		interceptors= boundInterceptors;


		InvocationContextImpl newCtx= new InvocationContextImpl(propertyURI, _self, parameters, interceptors, ctx.getFacets());
		Object result= newCtx.run();
		return (T)result;
	}
	
	//@AdvisesProperties("*") 
	public Object getAny(Object... p_parameters) {
		InvocationContext ctx= Meteor.getInvocationContext();
		
		String propertyURI= ctx.getMethodURI();
		ObjectImpl propertyObject= ObjectImpl.getObjectImpl(RepositoryImpl.findResourceByURI(_scope, propertyURI));

		Object value= _self.getValue(propertyURI);
		
		/*
		 * Don't return null for multivalued properties, return an empty container 
		 * instead.
		 */
		if (value == null && propertyObject != null) {
			Object isMany= propertyObject.getValue(MeteorNS.Property.many);
			if (Boolean.TRUE.equals(isMany)) {
				Object isQualified= propertyObject.getValue(MeteorNS.Property.indexed);
				if (Boolean.TRUE.equals(isQualified)) {
					value= new HashMap();
				}
				else {
					Object isUnique= propertyObject.getValue(MeteorNS.Property.unique);
					value= Boolean.TRUE.equals(isUnique) ? new LinkedHashSet() : new ArrayList();
				}
				_self.setValue(propertyURI, value);
			}
		}
		
//		/*
//		 * Coerce the result to the correct type 
//		 */
//		if (value != null && propertyObject != null) {
//			Object rangeObject= propertyObject.getValue(MeteorNS.Property.range);
//			if (rangeObject != null) {
//				String rangeURI= null;
//				if (rangeObject instanceof Resource) {
//					rangeURI= ((Resource)rangeObject).getURI();
//				}
//				else 
//					rangeURI= rangeObject.toString();
//
//				String rangeClassname= rangeURI.substring(rangeURI.indexOf(':')+1); // strip protocol
//				try {
//					Class<?> rangeClass= Activator.getMeteorClassloader().loadClass(rangeClassname);
//					if (!Boolean.TRUE.equals(propertyObject.getValue(MeteorNS.Property.many))) { 
//						if (!rangeClass.isAssignableFrom(value.getClass())) {
//							try {
//								value= repository.getService(ConversionService.class).convert(value, rangeClass);
//							}
//							catch (Throwable t) { // ignore conversion problems
//							}
//						}
//					}
//				} 
//				catch (ClassNotFoundException e) {
//					Logging.severe("Cannot find Type class:"+rangeClassname, e);
//				}
//			}
//		}
		
		return value;
	}
	
	@Override public void setProperty(String propertyURI, Object value, Object...parameters) {
		InvocationContextImpl ctx= (InvocationContextImpl) Meteor.getInvocationContext();
		Object[] args= ctx.getArguments().toArray();
		
		/**
		 * First lookup specializers in receiver itself (that is, look for 
		 * decorators added directly to the object, if any.
		 */
		List<Interceptor> interceptors= new ArrayList<Interceptor>();
		List<String> allTypes= _self._scope.getAllMeteorTypeURIs();
		for (Iterator<String> i= allTypes.iterator(); i.hasNext();) 
		{
			String typeURI= i.next();
			ParameterValueSpecializer specializer= _self._scope.findPropertySpecializer(typeURI, propertyURI);
			if (specializer != null)
				interceptors.addAll(specializer.findInterceptors(propertyURI, _self, args));
		}

		interceptors.addAll(__setPropertySpecializer.findInterceptors(propertyURI, _self, args));

		interceptors.add(0, __setAnyInterceptor);

		// get binding context and then remove interceptors that have any 
		// binding not in the current context.
		BindingContext bindingContext= ctx.getFacets();
		ArrayList<Interceptor> boundInterceptors= new ArrayList<Interceptor>();
		for (Interceptor interceptor : interceptors) {
			if (bindingContext.isSuperSetOf(interceptor.getBindingContext()))
				boundInterceptors.add(interceptor);
		}
		interceptors= boundInterceptors;

		InvocationContextImpl newCtx= new InvocationContextImpl(propertyURI, _self, new Object[] {value, parameters}, interceptors, ctx.getFacets());
		newCtx.run();
	}
	
	public void setAny(Object value, Object... p_parameters) {
		InvocationContext ctx= Meteor.getInvocationContext();
		String propertyURI= ctx.getMethodURI();
		
		Property property= RepositoryImpl.findResourceByURI(_scope, propertyURI, Property.class);
		boolean isMany= false;
		if (property != null) {
			
			if (property.isWriteOnce()) {
				Object oldValue= _self.getValue(propertyURI);
				if (oldValue != null)
					throw new MeteorException("Property is already set:"+propertyURI);
			}
			
			
			Class<?> rangeClass= property.getRange().getJavaType();
			
			if (isMany= property.isMany()) {
				if (property.isIndexed()) {
					Map collection= (Map)_self.getValue(propertyURI);
					if (collection == null) {
						collection= new HashMap();
						_self.setValue(propertyURI, collection);
					}
					if (value instanceof Map) {
						collection.clear();
						for (Iterator<Map.Entry> i= ((Map)value).entrySet().iterator();  i.hasNext();) {
							Map.Entry entry= i.next();
							collection.put(entry.getKey(), convertValue(rangeClass, entry.getValue()));
						}
					}
					else {
						if (p_parameters != null && 0 < p_parameters.length) {
							Object object = convertValue(rangeClass, value);
							Class<?> indexClass= property.getIndexedType().getJavaType();
							Object key= convertValue(indexClass, p_parameters[0]);
							collection.put(key, object);
						}
					}
					value= collection;
				}
				else {
					Collection collection= (Collection)_self.getValue(propertyURI);
					if (collection == null) {
						collection= property.isUnique() ? new LinkedHashSet() : new ArrayList();
						_self.setValue(propertyURI, collection);
					}
					if (value instanceof Collection) {
						collection.clear();
						for (Object element : (Collection)value) {
							element= convertValue(rangeClass, element);
							collection.add(element);
						}
					}
					else if (value != null && value.getClass().isArray() && rangeClass.isAssignableFrom(value.getClass().getComponentType())) {
						collection.clear();
						int len= Array.getLength(value);
						for (int i= 0; i < len; i++) {
							Object element= Array.get(value, i);
							element= convertValue(rangeClass, element);
							collection.add(element);
						}
					}
					else {
						value = convertValue(rangeClass, value);
						collection.add(value);
					}
					value= collection;
				}
			}
			else {
				value = convertValue(rangeClass, value);
				_self.setValue(propertyURI, value);
			}
			
			
			// set inverse properties
			Collection<Property> inverseProperties= property.getInverseOf();
			if (inverseProperties != null) // early in boot process it is possible for this to be null instead of empty set
				for (Property inverseProperty : inverseProperties) {
					Object[] allOthers= isMany ?  ((Collection)value).toArray() : new Object[] { value };
					for (Object otherObject : allOthers) {
						Resource other= (Resource)otherObject;
						Object inverseValue= other.getProperty(inverseProperty);
						if (inverseProperty.isMany()) {
							if (!((Collection)inverseValue).contains(_self))
								other.setProperty(inverseProperty, _self);
						}
						else {
							if (!_self.equals(inverseValue))
								other.setProperty(inverseProperty, _self);
						}
					}
				}
			
		}
		else
			_self.setValue(propertyURI, value);
		
	}

	private Object convertValue(Class<?> rangeClass, Object value) 
	{
		if (value != null) {
			if (!rangeClass.isAssignableFrom(value.getClass())) {
				try {
					if (_conversionService == null) 
						_conversionService= _scope.getInstance(ConversionService.class);
					value= _conversionService.convert(value, rangeClass);			
				}
				catch (Throwable t) { // ignore conversion problems
					t.printStackTrace();
				}
			}
		}
		return value;
	}
	
	
	/**
	 * Lazily set the type when requested.
	 * The type is set lazily because the Type object may not actually exist 
	 * at the time that the object is created.
	 */ 
	@Override public Type getType() {
		InvocationContext ctx= Meteor.getInvocationContext();
		Object objType= ctx.proceed();
		if (objType == null || !(objType instanceof Type)) {
			Type type= RepositoryImpl.findResourceByURI(_scope, _self._typeURI, Type.class);
			if (type != null) { 
				_self.setType(type);
				return type;
			}
		}
		return (Type) objType;
	}
	
	@Override public <T> T castTo(Class<T> roleClass) {
		T t= _self.findRole(roleClass);
		if (t != null)
			return t;
		
		t= _self.createRole(roleClass);
		
		return t;
	}

	@Override public java.util.Set<Property<?>> getContainedProperties() {
		Set<String> keys= _self.keySet();
		Set<Property<?>> properties= new LinkedHashSet<Property<?>>();
		Scope repository= _self.getScope();
		for (String key : keys)
			properties.add(RepositoryImpl.findResourceByURI(repository, key, Property.class));
			
		return properties;
	};
	
	
	@Override public void setProperty(Property property, Object value, Object...parameters) {
		setProperty(ObjectImpl.getObjectImpl(property).internalGetURI(), value, parameters);
	}
	
	@Override public <T> T getProperty(Property property, Object...parameters) {
		return (T)getProperty(ObjectImpl.getObjectImpl(property).internalGetURI(), parameters);
	}
	
	@Override public void addProperty(String propertyURI, Object value, Object...parameters) {
		setProperty(propertyURI, value, parameters);
	}
	
	@Override public void removeProperty(String propertyURI, Object value, Object...parameters) {
		Property property= RepositoryImpl.findResourceByURI(_scope, propertyURI, Property.class);
		if (property.isMany()) {
			
			// coerce to compatible value
			if (value != null) {
				Class<?> javaType= property.getRange().getJavaType();
				if (!javaType.isAssignableFrom(value.getClass()))
					value = _scope.getInstance(ConversionService.class).convert(value, javaType);
			}
			
			Collection<?> all= getProperty(propertyURI);
			all.remove(value);
		}
		else {
			setProperty(propertyURI, null);
		}
	}
	
	@Override public void clearProperty(String propertyURI) {
		Property property= RepositoryImpl.findResourceByURI(_scope, propertyURI, Property.class);
		if (property.isMany()) {
			Collection<?> all= getProperty(propertyURI);
			for (Object value : all)
				removeProperty(propertyURI, value);
		}
		else {
			setProperty(propertyURI, null);
		}
	}
	
	
	/**
	 * Returns all the roles of a given Type played by this object.
	 */
	@Override public Set<Resource> getRoles(Type type) {
		return (Set<Resource>) getRoles(type.getJavaType());
	}
	@Override public <T> Set<T> getRoles(Class<T> javaType) {
		Set<?> roles= getRoles();		
		Set<T> match= new LinkedHashSet<T>();
		for (Object role : roles) {
			if (role.getClass().isAssignableFrom(javaType))
				match.add((T) role);
		}
		return match;
	}
	@Override public <T> T getRole(Class<T> javaType) {
		Set<T> roles= getRoles(javaType);
		if (roles == null || roles.isEmpty())
			return null;
		return roles.iterator().next();
	}
	
	@Override public String getURI() {
		return _self.internalGetURI();
	}
	@Override public void setURI(String uri) {
		_self.internalSetURI(uri);
	}
	
	@Override public int hashCode() {
		return _self.internalGetURI().hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		if (obj instanceof Resource) {
			Resource self= Meteor.getInvocationContext().getReceiver();
			return self.getURI().equals(((Resource)obj).getURI());
		}
		return false;
	}
	@Override public void setType(Class javaType) {
		Type type= DomainImpl.findType(_scope, javaType);
		setProperty(MeteorNS.Resource.type, type);
	}
	
	@Override public <T> Collection<T> getPropertyValues(String propertyURI) {
		Object value= _self.getValue(propertyURI);
		if (value == null)
			return Collections.EMPTY_SET;

		if (value instanceof Collection<?>) 
			return new ArrayList<T>((Collection)value);
		
		if (value instanceof Map<?,?>) 
			return new ArrayList<T>(((Map)value).values());

		ArrayList<T> list= new ArrayList<T>(1);
		list.add((T)value);
		return list;
	}
	
	@Override public boolean isInstanceOf(Class<?> javaType)
	{
		return _self.isInstanceOf(DomainImpl.findType(_scope, javaType));
	}
	
	@Override public boolean isInstanceOf(Type type)
	{
		return type.isAssignableFrom(_self.getType());
	}

	
	@Override public <T> T addDecorator(Class<T> decoratorClass)
	{
		return _self.addDecorator(decoratorClass);
	}
	
	@Override public Scope getScope()
	{
		return _self._scope.internalCast(Scope.class);
	}
	
	@Override public String getLabel()
	{
		String label= (String)Meteor.proceed();
		if (label == null)
			return _self.getURI();
		return label;
	}
	
	@Override public void postConstruct()
	{
		// do nothing		
	}
	
	@Override public void setFacets(BindingContext bindingContext)
	{
		ObjectImpl.getObjectImpl(_self).setValue(MeteorNS.Resource.facets, bindingContext);
	}
	public void setFacets(String facetURI)
	{
		BindingType bindingType= null;
		Object object= _scope.findResourceByURI(facetURI);
		if (object != null) {
			if (object instanceof Resource) {
				Class javaType= ((Resource)object).getType().getJavaType();
				if (BindingType.class.isAssignableFrom(javaType)) {
					bindingType= ((Resource)object).castTo(BindingType.class);
				}
				else if (Type.class.isAssignableFrom(javaType)) {
					Class<BindingType> facetClass= ((Resource)object).castTo(Type.class).getJavaType();
					bindingType= _scope.getInstance(facetClass);
				}
			}
			
			if (bindingType != null) {
				BindingContext facets= _self.getFacets().union(bindingType);
				ObjectImpl.getObjectImpl(_self).setValue(MeteorNS.Resource.facets, facets);
				return;
			}
		}
		Meteor.proceed();
	}
}
