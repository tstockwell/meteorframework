package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.annotation.IsFunction;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Use this provider to connect to a specific storage system.
 * 
 * @author Ted Stockwell
 */
@ModelElement 
public interface StorageServiceProvider
extends Provider
{
	
	/**
	 * Use this method to connect to the 'default' storage system for 
	 * the current runtime environment.
	 * The URL of the default storage system is specified by the 
	 * StorageConfiguration.getDefaultConnectionURL method. 
	 */
	@IsFunction public StorageService getDefaultStorageService();

	/**
	 * This method is meant to be overloaded by all storage system implementations.
	 * The protocol of the given URL will indicate the type of connection to create.
	 * A storage implementation will check the given URL and if it supports the 
	 * indicated connection type then it will create a connection and return it. 
	 * If a storage implementation does not support the given URL then it will 
	 * call Meteor.proceed() to pass the request to the next implementation. 
	 */
	@IsFunction public StorageService getStorageService(String connectionURL);

}