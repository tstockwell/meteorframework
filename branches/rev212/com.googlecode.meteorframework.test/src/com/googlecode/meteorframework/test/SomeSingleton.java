package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.IsSingleton;
import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement 
@IsSingleton 
public interface SomeSingleton
extends Resource
{

}
