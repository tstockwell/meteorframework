package com.googlecode.meteorframework;


import com.googlecode.meteorframework.impl.MeteorInvocationException;




/**
 * An Interceptor is a wrapper around a java method that intercepts method 
 * invocations.
 * The java method associated with an Interceptor, called the 'handler method', 
 * should process the request and return a result.
 * 
 * The main purpose of an Interceptor is to translate a Model method call to 
 * an invocation of the underlying handler method.
 * 
 * Meteor processes method invocations with 'stacks' of Interceptors.
 * If a handler does not want to intercept a particular request then the 
 * handler can pass the invocation to the next handler in the stack by 
 * calling the InvocationContext.proceed method. 
 * 
 * A handler may alter or replace the arguments passed to a 
 * method or property by altering and/or replacing arguments in the list of 
 * arguments returned from the InvocationContext.getArguments method.
 * 
 * If a handler completely fulfills a particular request 
 * then it should return the result and NOT call the 
 * InvocationContext.proceed method.  
 * 
 * @author ted stockwell
 */
public interface Interceptor extends Comparable<Interceptor>
{
	/**
	 * Returns the underlying Java method, the 'handler' method.
	 */
	public java.lang.reflect.Method getHandlerMethod();

	/**
	 * Transform a Model method invocation into an invocation on the 
	 * underlying Java method.
	 */
	public Object process(InvocationContext context) 
		throws MeteorInvocationException;
	
	/**
	 * Return the Namespace to which this interceptor belongs. 
	 */
	public Namespace getNamespace();

	/**
	 * Returns the binding context in which the intercetor operates.
	 */
	public BindingContext getBindingContext();
}
