package com.googlecode.meteorframework.core;

import java.util.List;



/**
 * Encapsulates information about a Meteor method invocation.
 * A Meteor method is a method that is implemented by a stack of method 
 * interceptors that process the method invocation.
 * At runtime Meteor creates an appropriate stack of interceptors for each 
 * method invocation.
 * 
 * @see Interceptor
 * 
 * @author Ted Stockwell
 */
public interface InvocationContext {
	
	/**
	 * Invokes the next method in the overload chain.
	 * @return the invocation result  
	 */
	public Object proceed();

	public List<Object> getArguments();

	public Resource getReceiver();

	public Throwable getFault();

	/**
	 * @return the URI of the method that was invoked.
	 */
	public String getMethodURI();

	public Object getResult();

	/**
	 * Every method invocation is executed in a facet context 
	 * that is the union of the receiver's  facet context 
	 * and the facet context of the last invocation on the stack.  
	 */
	public BindingContext getBindingContext();
}
