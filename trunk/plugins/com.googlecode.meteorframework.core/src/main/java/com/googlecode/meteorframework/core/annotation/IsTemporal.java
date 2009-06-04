package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.MeteorNS;


/**
 * A convenience annotation for marking a property as temporal.
 * 
 * @author Ted Stockwell
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SemanticEquivalent({@Setting(property=MeteorNS.Property.temporal, value="true")})
public @interface IsTemporal 
{
}
