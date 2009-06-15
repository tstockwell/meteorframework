package com.googlecode.meteorframework.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.annotation.SemanticEquivalent;
import com.googlecode.meteorframework.security.SecurityNS;

/**
 * Denotes that the associated method or type requires the denoted permission 
 * in order to access.
 * 
 * @author ted stockwell
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@SemanticEquivalent({CoreNS.Resource.type, SecurityNS.ActionType.TYPE})
public @interface IsSecuredAction
{
}
