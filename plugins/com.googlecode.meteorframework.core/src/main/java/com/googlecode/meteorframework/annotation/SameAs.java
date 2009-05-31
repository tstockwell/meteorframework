package com.googlecode.meteorframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.MeteorNS;


/**
 * Marks a property or method as being equivalent to some other property or 
 * method.
 * Similiar in concept to the OWL:EquivalentTo.
 * 
 *  
 * @author ted stockwell
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SemanticEquivalent({@Setting(property=MeteorNS.Resource.sameAs, value="{$value}")})
public @interface SameAs 
{
	public String value();
	
}
