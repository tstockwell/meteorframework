package com.googlecode.meteorframework.impl.utils;

import com.googlecode.meteorframework.MeteorConversionException;
import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.core.utils.ConversionService;

/**
 * Utility for converting scalar values to objects of a specified Class.
 * Inspired by the Apache Commons Dynabeans ConvertUtils class but more general 
 * and is expanded by simply creating a Model MethodOverload of the Converter.convert method.
 * 
 * @author Ted Stockwell
 */
@Decorator public abstract class ConversionServiceImpl implements ConversionService {
	
	/**
	 * Convert the value to an object of the specified class (if possible).
	 * 
	 * @param value - Value to be converted (may be null)
	 * @param targetType - Class of the value to be converted to
	 * @return The converted value
	 * @throws MeteorException - if cannot convert to target type 
	 */
	@SuppressWarnings("unchecked")
	public <T> T convert(Object value, Class<T> type) 
	{
		if (value == null)
			return null;
		if (type.isAssignableFrom(value.getClass()))
			return (T)value;
		String msg= "No converter available.  ";
		msg+= "Cannot convert value of type "+value.getClass()+" to type "+type;
		throw new MeteorConversionException(msg);
	}
}
