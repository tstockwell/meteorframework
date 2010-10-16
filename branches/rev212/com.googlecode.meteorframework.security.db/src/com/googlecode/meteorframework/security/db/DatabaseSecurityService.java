package com.googlecode.meteorframework.security.db;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.security.SecurityService;
import com.googlecode.meteorframework.storage.StorageService;

/**
 * A security service that uses a Meteor StorageService to store data. 
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface DatabaseSecurityService
extends SecurityService
{
	@ModelElement public interface Constructor extends Service {
		public DatabaseSecurityService create(String serviceURL, StorageService storageService);
	}
	
	/**
	 * Pass this property in the connection URL to connect to a specific storage service;
	 * For instance...
	 * 	SecurityService service= securityServerFactory.create("meteor:security:db;storageURL=someURL");
	 */
	public static final String STORAGE_URL_PROPERTY= "storageURL";
	
	public StorageService getStorageService();
	
}
