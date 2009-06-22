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
	
	@Override public StorageSession openSession() 
	throws StorageException 
	{
		AppengineStorageSession session= _scope.createPrototype(AppengineStorageSession.class);
		session.setStorageService(_self);
		_scope.actualize(session);
		return session;
	}
}
