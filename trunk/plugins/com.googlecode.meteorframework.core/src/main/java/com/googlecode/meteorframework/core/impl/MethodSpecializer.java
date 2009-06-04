package com.googlecode.meteorframework.core.impl;

import java.util.List;

import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.Resource;


/**
 * A method specializer determines what interceptors are applicable 
 * to a particular method invocation.
 *  
 * @author ted stockwell
 */
public interface MethodSpecializer {
	
	/**
	 * returns the interceptors applicable to a given method invocation.
	 */
	public List<Interceptor> findInterceptors(String methodURI, Resource receiver, Object[] arguments);
	
}
