package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.annotation.IsSingleton;
import com.googlecode.meteorframework.annotation.Model;

@Model 
@IsSingleton 
public interface SomeSingleton
extends Resource
{

}
