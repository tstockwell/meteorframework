package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement
public interface StorageServiceDescriptor {
	
	public String getConnectionURL();
	
}
