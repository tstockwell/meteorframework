package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.CoreNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
@EquivalentMetadata({CoreNS.Resource.type, "{$value}"})
public @interface ElementType 
{
	public String value();
	
}
