package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.CoreNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@EquivalentMetadata({CoreNS.Resource.bindingContext})
@Model public @interface Bind
{
	public Class<? extends BindingType> value(); 
}
