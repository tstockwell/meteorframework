package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.annotation.Bind;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.binding.Testing;

@Model 
@Bind(Testing.class)
public interface SomeScopedType
{

}
