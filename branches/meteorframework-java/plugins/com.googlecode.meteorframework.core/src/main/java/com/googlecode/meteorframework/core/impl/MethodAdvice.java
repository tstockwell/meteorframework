package com.googlecode.meteorframework.core.impl;

import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Function;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;

/**
 * Implementations com.googlecode.meteorframework.Method.
 * 
 * @author ted stockwell
 */
@Decorator public abstract class MethodAdvice<T> implements Function<T>
{
	
	@Inject Scope _repository;
	@Decorates Function<T> _self;
	
	@Override public Type<?> getRange() {
		
		/**
		 * Unlike other properties which are always coerces to the correct type 
		 * in the set method, in order to boot Meteor we have to set types to 
		 * strings and then coerce them on the get method. 
		 */
		InvocationContext ctx= Meteor.getInvocationContext();
		Object objRange= ctx.proceed();
		if (objRange instanceof String) {
			Type<?> range= RepositoryImpl.findResourceByURI(_repository, (String)objRange, Type.class);
			if (range != null) {
				_self.setRange(range);
				return range;
			}
			return null;
		}
		return ObjectImpl.getObjectImpl(objRange).internalCast(Type.class);
	}

	public static Function<?> createMethod(Scope scope, java.lang.reflect.Method javaMethod)
	{
		Function<?> method= scope.getInstance(Function.class);
		method.setURI(Meteor.getURIForMethod(javaMethod));
		
		// set range
		Class<?> rangeClass= javaMethod.getReturnType();
		method.setRange(DomainImpl.findType(scope, rangeClass));
		RepositoryImpl.addResource(scope, method);
		
		return method;
	}
	
}
