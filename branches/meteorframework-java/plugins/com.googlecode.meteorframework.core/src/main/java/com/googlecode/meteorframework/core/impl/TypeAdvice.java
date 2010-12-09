package com.googlecode.meteorframework.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Function;
import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.annotation.IsSingleton;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.utils.Logging;

/**
 * Implementation of com.googlecode.meteorframework.Type.
 * 
 * These methods are implemented in this class instead of in the com.googlecode.meteorframework.* 
 * classes because these methods use code that is particular to the implementation 
 * in this package.  In order to avoid tying the Meteor model API to this particular 
 * implementation this code is kept out of the com.googlecode.meteorframework.* classes.
 * 
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
@IsSingleton 
@Decorator public abstract class TypeAdvice<T> implements Type<T>
{
	static WeakHashMap<ObjectImpl, Boolean> _initializedTypes= new WeakHashMap<ObjectImpl, Boolean>();
	
	 @Decorates private Type<T> _self;
	 @Inject private Scope _scope;
	 
	static Class<?> getJavaTypeFromTypeURI(String uri) {
		String className= getJavaClassNameTypeURI(uri);
		try {
			Class<?> class1= Activator.getMeteorClassloader().loadClass(className);
			return class1;
		}
		catch (ClassNotFoundException x) {
			if (className.equals("[B"))
				return byte[].class;
			if (className.equals("[C"))
				return char[].class;
			throw new MeteorException("Could not find Java class:"+className);
		}
	}
	static String getJavaClassNameTypeURI(String uri) {
		String className= uri;
		int i= className.indexOf(':');
		if (0 <= i)
			className= className.substring(i+1);
		return className;
	}
	
	@Override
	public Set<Property<?>> getDeclaredProperties()
	{
		// sort declared properties by fieldOrder
		Set<Property<?>> properties= Meteor.proceed();
		if (properties == null) // can be null early in boot process
			return Collections.EMPTY_SET;
		List<Property<?>> fieldOrder= _self.getFieldOrder();
		if (fieldOrder == null)
			return properties;
		ArrayList<Property<?>> list= new ArrayList<Property<?>>(properties);
		Collections.sort(list, new FieldOrderComparator(fieldOrder));
		return new LinkedHashSet(list); 
	}
	
	@Override public Class<T> getJavaType() {
		InvocationContext ctx= Meteor.getInvocationContext();
		ObjectImpl impl= ObjectImpl.getObjectImpl(ctx.getReceiver());
		return (Class<T>)getJavaTypeFromTypeURI(impl.internalGetURI());
	}
	
	@Override public boolean getScalar()
	{
		// get for native java types
		InvocationContext ctx= Meteor.getInvocationContext();
		ObjectImpl self= ObjectImpl.getObjectImpl(ctx.getReceiver());
		String uri= self.internalGetURI();
		int i= uri.indexOf(':');
		if (0 < i)
			uri= uri.substring(i+1);
		if (uri.startsWith("java.")) 
			return !uri.equals(Object.class.getName());  
		if (uri.startsWith("["))
			return true; // is a Java array
		
		return (Boolean)ctx.proceed();
	}
	
	@Override public boolean isSuperTypeOf(Type type) {
		final String uri= getURI();
		HashSet<Type> completed= new HashSet<Type>();
		ArrayList<Type> todo= new ArrayList<Type>();
		todo.add(type);
		while (!todo.isEmpty()) {
			Type superType= todo.remove(0);
			if (completed.contains(superType))
				continue;
			if (uri.equals(superType.getURI()))
				return true;
			completed.add(superType);
			todo.addAll(superType.getSubTypes());
		}
		return false;
	}
	
	/**
	 * Get all properties that have default values.
	 * Does not include properties inherited from supertypes. 
	 * @return
	 */
	@Override synchronized public Set<Property<?>> getDeclaredDefaultedProperties() {
		Set<Property<?>> defaultedProperties= new LinkedHashSet<Property<?>>();
		Set<Property<?>> declaredProperties= _self.getDeclaredProperties();
		if (declaredProperties != null) // can be null when booting up
			for (Property<?> property : declaredProperties) {
				Object defaultValue= property.getDefaultValue();
				if (defaultValue != null)
					defaultedProperties.add(property);
			}
		return defaultedProperties;
	}
	
	/**
	 * Get all properties that have default values.
	 * Includes properties inherited from supertypes. 
	 */
	@Override public Set<Property<?>> getAllDefaultedProperties() {
		Set<Property<?>> defaultedProperties= new LinkedHashSet();
		HashSet<Type> done= new HashSet<Type>();
		ArrayList<Type> todo= new ArrayList<Type>(_self.getSuperTypes());
		while (!todo.isEmpty()) {
			Type type= todo.remove(0);
			if (done.contains(type))
				continue;
			done.add(type);
			defaultedProperties.addAll(type.getDeclaredDefaultedProperties());
			todo.addAll(type.getSuperTypes());
		}
		defaultedProperties.addAll(_self.getDeclaredDefaultedProperties());
		return defaultedProperties;
	}
	
	
	/**
	 * Get all properties that have default values.
	 * Includes properties inherited from supertypes. 
	 */
	@Override public Set<Property<?>> getAllProperties() {
		Set<Property<?>> allProperties= new LinkedHashSet<Property<?>>();
		allProperties.addAll(_self.getDeclaredProperties());
		for (Type type : _self.getSuperTypes()) 
			allProperties.addAll(type.getAllProperties());
		for (Type type : _self.getExtensions()) 
			allProperties.addAll(type.getAllProperties());
		return allProperties;
	}
	
	@Override public boolean isAssignableFrom(Class<?> javaType)
	{
		Type type= DomainImpl.findType(_scope, javaType);
		return _self.isAssignableFrom(type);
	}
	
	@Override public boolean isAssignableFrom(Type<? extends T> type)
	{
		InvocationContext ctx= Meteor.getInvocationContext();
		Type<?> self= ObjectImpl.getObjectImpl(ctx.getReceiver()).internalCast(Type.class);
		
		ArrayList<Class<?>> allThisTypes= new ArrayList<Class<?>>();
		allThisTypes.add(self.getJavaType());
		for (Type type2 : self.getExtensions())
			allThisTypes.add(type2.getJavaType());
		for (Type type2 : self.getRolesPlayed())
			allThisTypes.add(type2.getJavaType());
		
		ArrayList<Class<?>> allThatTypes= new ArrayList<Class<?>>();
		allThatTypes.add(type.getJavaType());
		for (Type type2 : type.getExtensions())
			allThatTypes.add(type2.getJavaType());
		for (Type type2 : type.getRolesPlayed())
			allThatTypes.add(type2.getJavaType());
		
		for (Class<?> thisType : allThisTypes) {
			for (Class<?> thatType : allThatTypes) {
				if (thisType.isAssignableFrom(thatType)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Creates a meteor type based on a Java type.
	 * Uses Java reflection to examine the Java class and create associated 
	 * properties and methods for the Meteor type.
	 * 
	 * The Java class is expected to follow Java Bean conventions.  
	 */
	public static Type createType(Scope scope, Class javaType)
	{
		final String typeURI= Meteor.getURIForClass(javaType);
		
		Type type= RepositoryImpl.findResourceByURI(scope, typeURI, Type.class);
		if (type == null) {
			type= scope.getInstance(Type.class);
			type.setURI(typeURI);
			type.setLabel(typeURI.substring(typeURI.lastIndexOf('.')+1));
			RepositoryImpl.addResource(scope, type);
		}
		
		if (MeteorAnnotationUtils.isModeledObject(javaType)) {
			ObjectImpl impl= ObjectImpl.getObjectImpl(type);
			if (_initializedTypes.get(impl) == null) {
				_initializedTypes.put(impl, Boolean.TRUE);
				
				// set super type(s)...
				Class<?>[] superClasses= javaType.getInterfaces();
				// make Resource the super type of all interfaces 
				if (superClasses.length <= 0 && !javaType.equals(Resource.class)) 
					superClasses= new Class[] { Resource.class };
				for (Class<?> superClass : superClasses) {
					Type superType= createType(scope, superClass);
					type.setProperty(CoreNS.Type.superTypes, superType); 
				}
				
				initType(scope, type);
			}
		}
		
		return type;
	}
	
	static void initType(Scope repository, Type type) {
		
		Class<?> javaType= type.getJavaType();
		
		// If not a Model class then do nothing (the class is probably a native Java type).
		if (!javaType.isAnnotationPresent(ModelElement.class))
			return;
		
		// Add properties and methods from Java class
		Set<String> propertyURIs= new HashSet<String>();
		Set<Property<?>> properties= new LinkedHashSet<Property<?>>();
		Set<Function<?>> methods= new LinkedHashSet<Function<?>>();
		java.lang.reflect.Method[] declaredMethods= javaType.getDeclaredMethods();
		
		for (int i = 0; i < declaredMethods.length; i++) {
			java.lang.reflect.Method javaMethod= declaredMethods[i];
			
			String fieldName= javaMethod.getName();
			javaMethod.setAccessible(true);
			
			String uri= Meteor.getURIForMethod(javaMethod);
			if (MeteorAnnotationUtils.isMeteorMethod(javaMethod)) {
				Function<?> method= MethodAdvice.createMethod(repository, javaMethod);
				methods.add(method);
			}
			else if (MeteorAnnotationUtils.isMeteorProperty(javaMethod)) {
				if (!propertyURIs.contains(uri)) {
					fieldName= uri.substring(uri.lastIndexOf('.')+1);
					java.lang.reflect.Method getMethod= null;
					java.lang.reflect.Method setMethod= null;
					for (int j = 0; j < declaredMethods.length && (getMethod == null || setMethod == null); j++) {
						java.lang.reflect.Method method = declaredMethods[j];
						if (!MeteorAnnotationUtils.isMeteorProperty(method))
							continue;
						
						String propertyName= method.getName().substring(3);
						propertyName= propertyName.substring(0,1).toLowerCase()+propertyName.substring(1);					
						if (propertyName.equals(fieldName)) {
							if (getMethod == null) 
								if (method.getName().startsWith("get"))
									getMethod= method;
							if (setMethod == null)
								if (method.getName().startsWith("set"))
									setMethod= method;
						}
					}
					if (getMethod == null && setMethod == null) {
						Logging.severe("No get or set methods found for property "+uri);
						continue;
					}
					Property<?> property= PropertyImpl.createProperty(repository, getMethod, setMethod);
					properties.add(property);
					propertyURIs.add(uri);
				}
			}
		}
		
		type.setProperty(CoreNS.Type.declaredProperties, properties);
		type.setProperty(CoreNS.Type.declaredFunctions, methods);
	}
	
	
	public void setFieldOrder(Object value) {
		if (value instanceof String[]) {
			_self.setProperty(CoreNS.Type.fieldOrder, null);
			for (String v : ((String[])value)) {
				_self.setProperty(CoreNS.Type.fieldOrder, v);
			}
		}
		else
			Meteor.proceed();
	}
}
