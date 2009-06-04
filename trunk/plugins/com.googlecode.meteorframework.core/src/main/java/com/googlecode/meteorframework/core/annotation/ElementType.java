package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.MeteorNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
@SemanticEquivalent({@Setting(property=MeteorNS.Resource.type, value="{$value}")})
public @interface ElementType 
{
	public String value();
	
}
