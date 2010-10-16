package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement public interface StorageTask<T> 
{
	public T run(StorageSession session);
	
}
