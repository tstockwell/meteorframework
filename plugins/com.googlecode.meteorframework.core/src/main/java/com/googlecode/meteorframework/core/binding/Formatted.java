package com.googlecode.meteorframework.core.binding;

import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.annotation.IsSingleton;
import com.googlecode.meteorframework.core.annotation.Model;

/**
 * Represents a property or method that returns a formatted result. 
 * 
 * @author Ted Stockwell
 */
@Model
@IsSingleton
public interface Formatted
extends BindingType
{
	String format();
}
