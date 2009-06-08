package net.sf.meteor.storage.triplestore.impl;

import net.sf.meteor.storage.triplestore.TripleStoreService;
import net.sf.meteor.storage.triplestore.TripleStoreSession;

import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;

@Decorator abstract public class ProviderImpl 
implements Provider
{
	@Decorates Provider _self;
	
	public TripleStoreSession getInstance(Class<TripleStoreSession> javaType)
	{
		TripleStoreSession session= _self.getInstance(Resource.class).castTo(TripleStoreSession.class);
		session.setType(TripleStoreSession.class);
		return session;
	}
	
	public TripleStoreService getInstance(Class<TripleStoreService> javaType)
	{
		TripleStoreService service= _self.getInstance(Resource.class).castTo(TripleStoreService.class);
		((Resource)service).setType(TripleStoreService.class);
		return service;
	}
}
