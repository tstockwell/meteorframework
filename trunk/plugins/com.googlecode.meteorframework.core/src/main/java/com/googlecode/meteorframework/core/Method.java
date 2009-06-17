package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;

/**
 * A Meteor method is NOT like a Java method.
 * A Meteor method is a function call handler.
 * A method is woven into a function's call stack at runtime 
 * by the Meteor runtime engine.
 *
 * @param <T>  The type of object that this method returns.  Use java.lang.Void for void methods.
 * 
 * @author Ted Stockwell
 */
@com.googlecode.meteorframework.core.annotation.ModelElement 
public interface Method<T>
extends ModelElement
{
	
	/**
	 * Returns the functions that this method decorates
	 */
	@InverseOf(CoreNS.Function.decoratingMethods)
	Set<Function<T>> getDecoratedFunctions();
}
