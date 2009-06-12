package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.annotation.Model;

@Model
public interface StorageServiceDescriptor {
	
	public String getConnectionURL();
	
}
