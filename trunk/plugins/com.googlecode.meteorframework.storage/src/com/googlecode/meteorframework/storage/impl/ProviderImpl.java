package com.googlecode.meteorframework.storage.impl;

import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageServiceProvider;
import com.googlecode.meteorframework.storage.StorageSession;

@Decorator public abstract class ProviderImpl 
implements Provider
{
	@Decorates Provider _self;

	public StorageSession getInstance(Class<StorageSession> class1)
	{
		StorageService service= _self.getInstance(StorageService.class);
		return service.openSession();
	}
	
	public StorageService getInstance(Class<StorageService> class1)
	{
		return _self.getInstance(StorageServiceProvider.class).getDefaultStorageService();
	}
}
