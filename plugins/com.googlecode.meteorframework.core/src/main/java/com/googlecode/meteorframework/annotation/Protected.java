package com.googlecode.meteorframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When applied to a @Model method, indicates that the method may not be 
 * invoked by clients of a resource, only by the resource itself, 
 * like a protected method in Java.
 *    
 * @author Ted Stockwell
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Protected
{

}
