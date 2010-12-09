package com.googlecode.meteorframework.storage.appengine.impl;

import java.text.ParseException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.appengine.AppengineNS;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageService;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageSession;

@Decorator public abstract class AppengineStorageServiceImpl 
implements AppengineStorageService, Resource 
{
	@Inject DatastoreService _datastoreService;
	@Inject Scope _scope;
	@Decorates AppengineStorageService _self;
	
	@Override
	public void postConstruct() {
		
		String connectionURL= _self.getConnectionURL();
		AppengineConnectionURL appengineURL;
		try {
			appengineURL = new AppengineConnectionURL(connectionURL);
		} catch (ParseException e) {
			throw new StorageException("Bad connection URL, did you specify the root URI?", e);
		}
		
		String rootURI= appengineURL.getRootURI();
		Query query= new Query(CoreNS.Resource.TYPE);
		query.addFilter(CoreNS.Resource.uRI, FilterOperator.EQUAL, rootURI);
		Entity entity= _datastoreService.prepare(query).asSingleEntity();
		Key rootKey= entity.getKey();
		
		((Resource)_self).setProperty(AppengineNS.AppengineStorageService.datastoreService, _datastoreService);
		((Resource)_self).setProperty(AppengineNS.AppengineStorageService.rootKey, rootKey);
	}
	
	@Override public StorageSession openSession() 
	throws StorageException 
	{
		AppengineStorageSession session= _scope.createPrototype(AppengineStorageSession.class);
		session.setStorageService(_self);
		_scope.actualize(session);
		return session;
	}
}
