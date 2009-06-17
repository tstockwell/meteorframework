package com.googlecode.meteorframework.core.query;

import java.util.Set;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.ModelElement;


/**
 * Convenience methods for creating restrictiions.
 * @author Ted Stockwell
 */
@ModelElement public interface Restrictions extends Service {
	
	public Restriction uriEq(Resource reference);
	public Restriction uriEq(String uri);
	
	public <T> Restriction propertyEq(Property<T> property, T value);
	public Restriction propertyEq(String propertyURI, Object value);
	
	public <T> Restriction propertyLT(Property<T> property, T value);
	public Restriction propertyLT(String propertyURI, Object value);
	
	public <T> Restriction propertyGT(Property<T> property, T value);
	public Restriction propertyGT(String propertyURI, Object value);
	
	public <T> Restriction propertyLike(Property<T> property, T value);
	public Restriction propertyLike(String propertyURI, Object value);
	
	public <T> Restriction propertyGE(Property<T> property, T value);
	public Restriction propertyGE(String propertyURI, Object value);
	
	public <T> Restriction propertyLE(Property<T> property, T value);
	public Restriction propertyLE(String propertyURI, Object value);
	
	public <T> Restriction propertyNE(Property<T> property, T value);
	public Restriction propertyNE(String propertyURI, Object value);
	
	public <T> Restriction propertyIsNull(Property<T> property);
	public Restriction propertyIsNull(String propertyURI);

	
	public Restriction typeEq(Type<?> value);
	
	public Restriction or(Restriction restriction1, Restriction restriction2);

	public Restriction and(Restriction restriction1, Restriction restriction2);

	public Restriction not(Restriction restriction);

	public <T> Restriction propertyIn(Property<T> property, Set<T> value);
	public Restriction propertyIn(String propertyURI, Set<?> values);
}
