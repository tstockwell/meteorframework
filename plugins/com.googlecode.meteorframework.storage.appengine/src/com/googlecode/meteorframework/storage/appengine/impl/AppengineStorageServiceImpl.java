package com.googlecode.meteorframework.storage.appengine.impl;

import javax.annotation.PostConstruct;

import com.google.appengine.api.datastore.DatastoreService;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageService;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageSession;

@Decorator public abstract class AppengineStorageServiceImpl 
implements AppengineStorageService, Resource 
{
	@Inject DatastoreService _datastoreService;
	@Inject Scope _scope;
	@Decorates AppengineStorageService _self;
	
	Scope _storageScope;
	
	@PostConstruct
	public void postConstruct()
	{
		_storageScope= _scope.createScope();
		_storageScope.putInstance(_datastoreService, DatastoreService.class);
		_storageScope.putInstance(_self, AppengineStorageService.class);
	}
	
	
	@Override public StorageSession openSession() 
	throws StorageException 
	{
		return _storageScope.createInstance(AppengineStorageSession.class);
	}
}
