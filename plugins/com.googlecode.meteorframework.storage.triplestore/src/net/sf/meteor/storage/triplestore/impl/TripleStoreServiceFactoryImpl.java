package net.sf.meteor.storage.triplestore.impl;

import java.util.HashMap;

import net.sf.meteor.storage.triplestore.TripleStoreConfiguration;
import net.sf.meteor.storage.triplestore.TripleStoreService;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageServiceProvider;

/**
 * This decorator hooks into StorageServiceFactory to create a 
 * TripleStoreConnector when the connection URL begins with  TRIPLESTORE_PROTOCOL
 */
@Decorator public abstract class TripleStoreServiceFactoryImpl
implements StorageServiceProvider
{

	static HashMap<String, StorageService> __connectors= new HashMap<String, StorageService>();

	@Inject private Scope _scope;

	@Override synchronized public StorageService getStorageService(String connectionURL) {
		if (!connectionURL.startsWith(TripleStoreConfiguration.TRIPLESTORE_PROTOCOL))
			return (StorageService)Meteor.proceed();

		StorageService storageService= __connectors.get(connectionURL);
		if (storageService != null)
			return storageService;


		storageService= _scope.createInstance(TripleStoreService.class);
		storageService.setConnectionURL(connectionURL);
		__connectors.put(connectionURL, storageService);

		return storageService;
	}
}
