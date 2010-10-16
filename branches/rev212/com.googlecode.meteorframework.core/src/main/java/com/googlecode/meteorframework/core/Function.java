package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;


/**
 * Represents a generic function
 * 
 * @param <T>  The type of object that this method returns.  Use java.lang.Void for void methods.
 */
@com.googlecode.meteorframework.core.annotation.ModelElement 
public interface Function<T> 
extends Resource, ModelElement 
{
	
	/**
	 * The set of Types to which this method belongs.
	 * May be empty.
	 */
	@InverseOf(CoreNS.Type.declaredFunctions) 
	public abstract Set<Type<?>> getDomain();

	public abstract Type<?> getRange();
	public abstract void setRange(Type<?> p_type);

	/**
	 * Denotes the Methods that decorate this function.
	 */
	@InverseOf(CoreNS.Method.decoratedFunctions) 
	public abstract Set<Method<T>> getDecoratingMethods();

	/**
	 * Invoke this method.
	 */
	public abstract Object invoke(Resource receiver, Object... arguments);
}

