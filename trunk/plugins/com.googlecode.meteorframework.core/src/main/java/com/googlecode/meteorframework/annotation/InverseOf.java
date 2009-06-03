package com.googlecode.meteorframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.MeteorNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SemanticEquivalent({@Setting(property=MeteorNS.Property.inverseOf, value="{$value}")})
public @interface InverseOf 
{
	public String value();
	
}