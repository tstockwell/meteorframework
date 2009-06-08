package com.googlecode.meteorframework.storage.test;

import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.storage.StorageConfiguration;
import com.googlecode.meteorframework.storage.triplestore.TripleStoreConfiguration;

@Decorator public abstract class StorageTestConfiguration 
implements StorageConfiguration 
{
	@Override public String getDefaultConnectionURL() {
		// use memory-based triplestore 
		return TripleStoreConfiguration.TRIPLESTORE_PROTOCOL+"jdbc:h2:mem:test";		
	}
}
