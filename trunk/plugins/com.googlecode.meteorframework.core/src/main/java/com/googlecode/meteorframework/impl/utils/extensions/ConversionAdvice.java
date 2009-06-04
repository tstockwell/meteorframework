package com.googlecode.meteorframework.impl.utils.extensions;

import java.math.BigDecimal;

import com.googlecode.meteorframework.BindingType;
import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.Type;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.core.utils.ConversionService;
import com.googlecode.meteorframework.impl.ObjectImpl;
import com.googlecode.meteorframework.impl.RepositoryImpl;


/**
 * Basic conversion functionality
 * @author Ted Stockwell
 *
 */
@SuppressWarnings("unchecked")
@Decorator public abstract class ConversionAdvice implements ConversionService {
	
	@Inject Scope _scope;
	
	public Object convert(String resourceURI, Class<?> javaType) {
		try {
			Resource resource= RepositoryImpl.findResourceByURI(_scope, resourceURI);
			if (resource != null) 
				return resource.castTo(javaType);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		return Meteor.proceed();
	}
	
	public String convert(Resource resource, Class<String> javaType) {
		if (resource == null)
			return null;
		return ObjectImpl.getObjectImpl(resource).internalGetURI();
	}

	public <T extends Resource> T convert(Resource resource, Class<T> javaType) {
		return resource.castTo(javaType);
	}

	public <T> Type<T> convert(Class<T> javaType, Class<Type<T>> meteorType) {
		Type<T> type= _scope.findType(javaType);
		if (type != null)
			return type;
		return (Type<T>)Meteor.proceed();
	}
	
	public <T extends BindingType> T convert(String resourceURI, Class<T> javaType) {
		try {
			Resource resource= RepositoryImpl.findResourceByURI(_scope, resourceURI);
			if (resource != null) 
				return resource.castTo(javaType);
		}
		catch (Throwable t) {
		}
		
		try {
			return _scope.createInstance(javaType);
		}
		catch (Throwable t) {
		}
		
		return (T) Meteor.proceed();
	}
	
	public Boolean convert(String value, Class<Boolean> javaType) {
		return Boolean.parseBoolean(value);
	}

	public String convert(Boolean value, Class<String> javaType) {
		return value.toString();
	}

	public Integer convert(String value, Class<Integer> javaType) {
		return Integer.parseInt(value);
	}

	public String convert(Integer value, Class<String> javaType) {
		return value.toString();
	}

	public BigDecimal convert(String value, Class<BigDecimal> javaType) {
		return new BigDecimal(value);
	}
	
	public String convert(BigDecimal value, Class<String> javaType) {
		return value.toString();
	}

	public Long convert(String value, Class<Long> javaType) {
		return Long.parseLong(value);
	}
	
	public String convert(Long value, Class<String> javaType) {
		return value.toString();
	}

	public Double convert(String value, Class<Double> javaType) {
		return Double.parseDouble(value);
	}
	
	public String convert(Double value, Class<String> javaType) {
		return value.toString();
	}
}
