package com.googlecode.meteorframework.security.db.impl;

import java.util.HashMap;

import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.security.SecurityService;
import com.googlecode.meteorframework.security.SecurityServiceFactory;
import com.googlecode.meteorframework.security.db.DBSecurityConfiguration;
import com.googlecode.meteorframework.security.db.DatabaseSecurityService;
import com.googlecode.meteorframework.storage.StorageConfiguration;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageServiceProvider;

/**
 * This decorator hooks into SecurityServiceFactory to create a 
 * DatabaseSecurityService when the connection URL begins with 'meteor:security:db'.
 */
@Decorator abstract public class DatabaseSecurityServiceFactoryImpl
implements SecurityServiceFactory
{
	static HashMap<String, DatabaseSecurityService> __services= new HashMap<String, DatabaseSecurityService>();
	
	@Inject private InvocationContext _invocationContext;
	@Inject private StorageServiceProvider _storageFactory;
	@Inject private StorageConfiguration _storageConfiguration;
	@Inject private DatabaseSecurityService.Constructor _factory;
	
	@Override public SecurityService create(String connectionURL) {
		
		// if not supported protocol then pass the request along 
		if (!connectionURL.startsWith(DBSecurityConfiguration.DB_SECURITY_PROTOCOL))
			return (SecurityService) _invocationContext.proceed();
		
		// check for cached service
		DatabaseSecurityService securityService= __services.get(connectionURL);
		if (securityService != null)
			return securityService;
				
		// extract storage connection URL
		String storageURL= _storageConfiguration.getDefaultConnectionURL();
		int i= connectionURL.indexOf(DatabaseSecurityService.STORAGE_URL_PROPERTY);
		if (0 <= i) {
			storageURL= connectionURL.substring(i);
			storageURL= storageURL.substring(storageURL.indexOf('=')+1);
			int j= storageURL.indexOf(';');
			if (0 <= j)
				storageURL= storageURL.substring(0, j);
		}
		
		StorageService storageService= _storageFactory.getStorageService(storageURL);
		securityService= _factory.create(connectionURL, storageService);
		__services.put(connectionURL, securityService);
		
		return securityService;
	}

}
