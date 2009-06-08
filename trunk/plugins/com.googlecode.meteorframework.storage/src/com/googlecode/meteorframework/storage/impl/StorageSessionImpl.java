package com.googlecode.meteorframework.storage.impl;

import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageNS;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageSession;

@Decorator public abstract class StorageSessionImpl 
{
	@Decorator public static abstract class StorageSessionConstructorImpl 
	implements StorageSession.Constructor
	{
		@Inject Scope _scope;

		@Override public StorageSession create(StorageService connector) {
			StorageSession session= _scope.createInstance(StorageSession.class);
			session.setProperty(StorageNS.StorageSession.connector, connector);
			return session;
		}
	}
	
}
