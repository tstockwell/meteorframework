package com.googlecode.meteorframework.core.binding;

import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Represents a property or method that returns a formatted result. 
 * 
 * @author Ted Stockwell
 */
@ModelElement
public class Formatted
extends BindingType
{
	String getFormat()
	{
		return (String)getProperty(BindingNS.Formatted.format);
	}
}
