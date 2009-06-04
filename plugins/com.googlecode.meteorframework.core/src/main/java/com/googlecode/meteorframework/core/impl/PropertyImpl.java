package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.utils.Logging;

/**
 * Implementations net.sf.Property.
 * 
 * These methods are implemented in this class instead of in the com.googlecode.meteorframework.* 
 * classes because these methods use code that is particular to the implementation 
 * in this package.  In order to avoid tying the Model API to this particular 
 * implementation this code is kept out of the com.googlecode.meteorframework.* classes.
 * 
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
@Decorator public abstract class PropertyImpl<T> implements Property<T>
{

	@Inject private Scope _scope;
	@Decorates private Property _self;
	
	@Override public Type getRange() {
		Object objRange= Meteor.proceed();
		if (objRange instanceof String) {
			Type type= RepositoryImpl.findResourceByURI(_scope, (String)objRange, Type.class);
			if (type != null) {
				_self.setProperty(MeteorNS.Property.range, type);
				return type;
			}
			return null;
		}
		return ObjectImpl.getObjectImpl(objRange).internalCast(Type.class);
	}
	
	
	public void addInverseOf(Property<T> inverseProperty) {
		if (!_self.isReference())
			throw new MeteorException("Only reference properties may have inverse properties");
		Meteor.proceed();
	}


	public static Property<?> createProperty(Scope scope, Method getMethod, Method setMethod)
	{
		
		String uri= Meteor.getURIForMethod((getMethod == null) ? setMethod : getMethod);
		Property<?> property= RepositoryImpl.findResourceByURI(scope, uri, Property.class);
		if (property == null) {
			property= scope.getInstance(Property.class);
			property.setURI(uri);
			String label= uri.substring(uri.lastIndexOf('.')+1);
			label= label.substring(0, 1).toUpperCase()+label.substring(1);
			property.setLabel(label);
		}
		
		Class<?> javaParameterClass= null;
		java.lang.reflect.Type parameterGenericType= null;
		if (getMethod == null) {
			property.setWriteOnly(true);
		}
		else {
			javaParameterClass= getMethod.getReturnType();
			parameterGenericType= getMethod.getGenericReturnType();
		}
		if (setMethod == null) {
			property.setReadOnly(true);
		}
		else if (javaParameterClass == null) {
			Class<?>[] classes= setMethod.getParameterTypes();
			if (classes.length <= 0) {
				Logging.warning("A set method has no parameters:"+setMethod.toGenericString()+".  This method should probably be annotated with @IsMethod");
			}
			else {
				javaParameterClass= classes[classes.length - 1];
				parameterGenericType= setMethod.getGenericParameterTypes()[classes.length - 1];
			}
		}
		
		// set range
		Class<?> rangeClass= javaParameterClass;
		if (Collection.class.isAssignableFrom(rangeClass)) {
			while (parameterGenericType != null && !(parameterGenericType instanceof ParameterizedType)) {
				if (parameterGenericType instanceof Class) {
					parameterGenericType= ((Class)parameterGenericType).getGenericSuperclass();
				}
				else
					break;
			}
			if (parameterGenericType instanceof ParameterizedType) {
				ParameterizedType parameterizedType= (ParameterizedType)parameterGenericType;
				java.lang.reflect.Type actualType= parameterizedType.getActualTypeArguments()[0];
				if (actualType instanceof Class) { 
					rangeClass= (Class<?>)actualType;
				}
				else if (actualType instanceof ParameterizedType)
					rangeClass= (Class<?>)((ParameterizedType)actualType).getRawType();
			}
			
			if (List.class.isAssignableFrom(rangeClass)) 
				property.setIndexedType(DomainImpl.findType(scope, Integer.class));
		}
		else if (Map.class.isAssignableFrom(rangeClass)) {
			while (parameterGenericType != null && !(parameterGenericType instanceof ParameterizedType)) {
				if (parameterGenericType instanceof Class) {
					parameterGenericType= ((Class)parameterGenericType).getGenericSuperclass();
				}
				else
					break;
			}
			if (parameterGenericType instanceof ParameterizedType) {
				ParameterizedType parameterizedType= (ParameterizedType)parameterGenericType;
				
				java.lang.reflect.Type actualType= parameterizedType.getActualTypeArguments()[1];
				if (actualType instanceof Class) { 
					rangeClass= (Class<?>)actualType;
				}
				else if (actualType instanceof ParameterizedType)
					rangeClass= (Class<?>)((ParameterizedType)actualType).getRawType();
				
				Class<?> indexClass= null;
				java.lang.reflect.Type actualIndexType= parameterizedType.getActualTypeArguments()[0];
				if (actualIndexType instanceof Class) { 
					indexClass= (Class<?>)actualIndexType;
				}
				else if (actualIndexType instanceof ParameterizedType)
					indexClass= (Class<?>)((ParameterizedType)actualIndexType).getRawType();
				if (indexClass != null) {
					Type indexType= DomainImpl.findType(scope, indexClass);
					property.setIndexedType(indexType);
				}
			}
		}
		
		Type rangeType= DomainImpl.findType(scope, rangeClass);
		property.setRange(rangeType);
		
		// set multivalue properties
		if (Collection.class.isAssignableFrom(javaParameterClass)) {
			property.setMany(true);
			if (Set.class.isAssignableFrom(javaParameterClass)) 
				property.setUnique(true);
			if (List.class.isAssignableFrom(javaParameterClass))
				property.setOrdered(true);
		}
		else if (Map.class.isAssignableFrom(javaParameterClass)) {
			property.setMany(true);
			property.setIndexed(true);
		}
		
		if (Resource.class.isAssignableFrom(rangeClass)) 
			property.setReference(true);
		
		RepositoryImpl.addResource(scope, property);

		return property;
	}
	
}
