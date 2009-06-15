package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * Represents a generic function
 * 
 * @param <T>  The type of object that this method returns.  Use java.lang.Void for void methods.
 */
@Model public interface Method<T> extends Resource, ModelElement {
	
	/**
	 * The set of Types to which this method belongs.
	 * May be empty.
	 */
	@InverseOf(CoreNS.Type.declaredMethods) 
	public abstract Set<Type<?>> getDomain();

	public abstract Type<?> getRange();
	public abstract void setRange(Type<?> p_type);

	/**
	 * Denotes the Methods that this Method advises.
	 */
	@InverseOf(CoreNS.Method.advisedBy) 
	public abstract Set<Method<T>> getAdvises();

	/**
	 * Denotes the Methods that advise this Method.
	 */
	@InverseOf(CoreNS.Method.advises) 
	public abstract Set<Method<T>> getAdvisedBy();

	/**
	 * Invoke this method.
	 */
	public abstract Object invoke(Resource receiver, Object... arguments);
}

