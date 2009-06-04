package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.MeteorNS;


/**
 * A convenience annotation for telling Meteor that a method that begins with 
 * 'get', 'set', or 'is' that the method is NOT an access method for a property
 * (and this represented in Meteor as a property) but is a real method (and thus 
 * should be represented in Meteor as a Method instead of a Property).
 *  
 * @author Ted Stockwell
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SemanticEquivalent({@Setting(property=MeteorNS.Resource.type, value=MeteorNS.Method.TYPE)})
public @interface IsMethod 
{
}
