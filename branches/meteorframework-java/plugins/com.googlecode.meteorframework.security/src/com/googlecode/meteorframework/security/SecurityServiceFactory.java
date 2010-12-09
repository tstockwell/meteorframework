package com.googlecode.meteorframework.security;

import com.googlecode.meteorframework.core.annotation.ModelElement;


/**
 * Use this factory to connect to a specific security system.
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface SecurityServiceFactory
{
	
	/**
	 * Use this method to connect to the 'default' storage system for 
	 * the current runtime environment.
	 * The URL of the default storage system is specified by the 
	 * StorageConfiguration.getDefaultConnectionURL method. 
	 */
	public SecurityService create();
	
	/**
	 * This method is meant to be overloaded by all security system implementations.
	 * The protocol of the given URL will indicate the type of connection to create.
	 * A security implementation will check the given URL and if it supports the 
	 * indicated connection type then it will create a connection and return it. 
	 * If a security implementation does not support the given URL then it will 
	 * call Meteor.proceed() to pass the request to the next implementation. 
	 */
	public SecurityService create(String connectionURL);

}