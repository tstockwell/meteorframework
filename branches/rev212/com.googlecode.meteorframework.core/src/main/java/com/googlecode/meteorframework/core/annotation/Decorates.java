package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@InjectionType
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Decorates
{

}
