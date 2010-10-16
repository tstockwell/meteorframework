package com.googlecode.meteorframework.core.binding;

import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.annotation.IsSingleton;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Represents code used when a system is in 'test' mode. 
 * 
 * @author Ted Stockwell
 */
@ModelElement
@IsSingleton
public class Testing
extends BindingType
{
}
