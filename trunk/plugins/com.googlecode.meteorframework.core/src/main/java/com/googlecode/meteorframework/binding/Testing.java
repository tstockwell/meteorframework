package com.googlecode.meteorframework.binding;

import com.googlecode.meteorframework.BindingType;
import com.googlecode.meteorframework.annotation.IsSingleton;
import com.googlecode.meteorframework.annotation.Model;

/**
 * Represents code used when a system is in 'test' mode. 
 * 
 * @author Ted Stockwell
 */
@Model
@IsSingleton
public class Testing
extends BindingType
{
}
