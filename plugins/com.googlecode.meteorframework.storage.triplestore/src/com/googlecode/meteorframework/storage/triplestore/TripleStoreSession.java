package com.googlecode.meteorframework.storage.triplestore;


import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.triplestore.impl.TripleStoreImpl;

@Model public interface TripleStoreSession extends StorageSession
{

	public void init(TripleStoreService tripleStoreServiceImpl, TripleStoreImpl storeImpl);

}
