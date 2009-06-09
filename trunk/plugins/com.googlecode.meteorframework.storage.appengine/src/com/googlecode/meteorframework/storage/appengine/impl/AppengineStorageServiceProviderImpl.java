package com.googlecode.meteorframework.storage.appengine.impl;

import java.util.HashMap;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageServiceProvider;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageConfiguration;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageService;

/**
 * This decorator hooks into StorageServiceFactory to create a 
 * AppengineStorageTripleStoreConnector when the connection URL begins with  TRIPLESTORE_PROTOCOL
 */
@Decorator public abstract class AppengineStorageServiceProviderImpl
implements StorageServiceProvider
{

	static HashMap<String, StorageService> __connectors= new HashMap<String, StorageService>();

	@Inject private Scope _scope;

	@Override synchronized public StorageService getStorageService(String connectionURL) {
		if (!connectionURL.startsWith(AppengineStorageConfiguration.APPENGINE_STORAGE_PROTOCOL))
			return (StorageService)Meteor.proceed();

		StorageService storageService= __connectors.get(connectionURL);
		if (storageService != null)
			return storageService;


		storageService= _scope.createInstance(AppengineStorageService.class);
		storageService.setConnectionURL(connectionURL);
		__connectors.put(connectionURL, storageService);

		return storageService;
	}
}
