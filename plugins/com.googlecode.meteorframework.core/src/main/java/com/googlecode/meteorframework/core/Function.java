package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * Represents a generic function
 * 
 * @param <T>  The type of object that this method returns.  Use java.lang.Void for void methods.
 */
@Model public interface Function<T> extends Resource, ModelElement {
	
	/**
	 * The set of Types to which this method belongs.
	 * May be empty.
	 */
	@InverseOf(CoreNS.Type.declaredFunctions) 
	public abstract Set<Type<?>> getDomain();

	public abstract Type<?> getRange();
	public abstract void setRange(Type<?> p_type);

	/**
	 * Denotes the Methods that this Method advises.
	 */
	@InverseOf(CoreNS.Function.advisedBy) 
	public abstract Set<Function<T>> getAdvises();

	/**
	 * Denotes the Methods that advise this Method.
	 */
	@InverseOf(CoreNS.Function.advises) 
	public abstract Set<Function<T>> getAdvisedBy();

	/**
	 * Invoke this method.
	 */
	public abstract Object invoke(Resource receiver, Object... arguments);
}

