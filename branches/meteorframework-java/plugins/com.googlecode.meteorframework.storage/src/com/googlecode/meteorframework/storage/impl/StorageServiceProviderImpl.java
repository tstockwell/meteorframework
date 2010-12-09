package com.googlecode.meteorframework.storage.impl;

import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageConfiguration;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageServiceProvider;


@Decorator  
public abstract class StorageServiceProviderImpl
implements StorageServiceProvider 
{

	@Inject StorageConfiguration _configuration;
	@Decorates StorageServiceProvider _self;

	@Override public StorageService getDefaultStorageService() {
		return _self.getStorageService(_configuration.getDefaultConnectionURL());
	}

	@Override public StorageService getStorageService(String connectionURL) {
		throw new StorageException("Cannot create connection to storage system "+connectionURL);
	}
}
