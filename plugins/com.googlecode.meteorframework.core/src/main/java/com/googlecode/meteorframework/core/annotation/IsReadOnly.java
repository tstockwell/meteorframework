package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.CoreNS;


/**
 * A convenience annotation for marking a property as read-only.
 * Mostly used when the property returns a collection of objects and the 
 * returned collection should be read-only.
 * 
 * @author Ted Stockwell
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@EquivalentMetadata({CoreNS.Property.readOnly, "true"})
public @interface IsReadOnly 
{
}
