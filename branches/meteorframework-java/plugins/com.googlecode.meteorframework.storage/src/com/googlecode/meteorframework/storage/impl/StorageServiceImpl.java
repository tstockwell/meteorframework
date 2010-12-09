package com.googlecode.meteorframework.storage.impl;

import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.StorageTask;

@Decorator 
abstract public class StorageServiceImpl 
implements StorageService
{
	@Decorates StorageService _self;
	
	@Override
	public <T> T run(StorageTask<T> task)
	{
		StorageSession session= _self.openSession();
		T result= null;
		try {
			result= task.run(session);
		}
		finally {
			session.close();
		}
		return result;
	}
}
