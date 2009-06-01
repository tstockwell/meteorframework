package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.annotation.Bind;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.binding.Testing;

@Model 
@Bind(Testing.class)
public interface SomeScopedType
{

}
