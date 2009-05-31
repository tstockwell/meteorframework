package com.googlecode.meteorframework.utils;

import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.TypeLiteral;
import com.googlecode.meteorframework.annotation.Model;

/**
 * Utility for creating object adapters.
 * Inspired by the Eclipse AdapterManager but more easily expanded by with 
 * Meteor Decorators.
 * 
 * @author Ted Stockwell
 */
@Model public interface AdapterService extends Service {
	
	/**
	 * Create an adapter of the specified class (if possible).
	 * 
	 * @param object - Object to be adapted.
	 * @param targetType - Type of adapter.
	 * @return The adapter
	 * @throws MeteorException - if cannot adapt to target type 
	 */
	public <T> T adapt(Object object, Class<T> targetType);
	
	public <T> T adapt(Object object, TypeLiteral<T> targetType);
}
