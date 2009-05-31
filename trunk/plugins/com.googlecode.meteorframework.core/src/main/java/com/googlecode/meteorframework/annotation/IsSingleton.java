package com.googlecode.meteorframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.MeteorNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE })
@SemanticEquivalent({@Setting(property=MeteorNS.Type.singleton, value="true")})
public @interface IsSingleton 
{
}
