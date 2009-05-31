package com.googlecode.meteorframework.utils;

import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;

/**
 * Utility for converting scalar values to objects of a specified Class.
 * Inspired by the Apache Commons Dynabeans ConvertUtils class but more general 
 * and is easily expanded by with Meteor Decorators.
 * 
 * @author Ted Stockwell
 */
@Model public interface ConversionService extends Service {
	
	/**
	 * Convert the value to an object of the specified class (if possible).
	 * 
	 * @param value - Value to be converted (may be null)
	 * @param targetType - Class of the value to be converted to
	 * @return The converted value
	 * @throws MeteorException - if cannot convert to target type 
	 */
	public <T> T convert(Object value, Class<T> type);
}
