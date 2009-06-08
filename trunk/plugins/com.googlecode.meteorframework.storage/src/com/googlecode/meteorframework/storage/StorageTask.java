package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.annotation.Model;

@Model public interface StorageTask<T> 
{
	public T run(StorageSession session);
	
}
