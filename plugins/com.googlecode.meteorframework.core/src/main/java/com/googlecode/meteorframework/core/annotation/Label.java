package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.MeteorNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE})
@SemanticEquivalent({@Setting(property=MeteorNS.Resource.label, value="{$value}")})
public @interface Label 
{
	public String value();
	
}
