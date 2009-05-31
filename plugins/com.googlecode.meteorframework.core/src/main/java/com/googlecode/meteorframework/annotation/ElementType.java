package com.googlecode.meteorframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.MeteorNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
@SemanticEquivalent({@Setting(property=MeteorNS.Resource.type, value="{$value}")})
public @interface ElementType 
{
	public String value();
	
}
