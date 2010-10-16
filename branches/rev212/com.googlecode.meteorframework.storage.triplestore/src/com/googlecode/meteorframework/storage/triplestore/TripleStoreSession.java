package com.googlecode.meteorframework.storage.triplestore;


import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.triplestore.impl.TripleStoreImpl;

@ModelElement public interface TripleStoreSession extends StorageSession
{

	public void init(TripleStoreService tripleStoreServiceImpl, TripleStoreImpl storeImpl);

}
