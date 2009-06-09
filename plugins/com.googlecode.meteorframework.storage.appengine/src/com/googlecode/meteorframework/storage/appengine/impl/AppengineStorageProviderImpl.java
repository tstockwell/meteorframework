package com.googlecode.meteorframework.storage.appengine.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.annotation.Decorator;

@Decorator
public abstract class AppengineStorageProviderImpl
implements Provider
{
	public DatastoreService createInstance(Class<DatastoreService> javaType, BindingType... bindings) {
		return DatastoreServiceFactory.getDatastoreService();
	}
}
