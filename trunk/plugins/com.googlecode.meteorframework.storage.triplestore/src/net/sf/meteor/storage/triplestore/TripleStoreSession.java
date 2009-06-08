package net.sf.meteor.storage.triplestore;

import net.sf.meteor.storage.triplestore.impl.TripleStoreImpl;

import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.storage.StorageSession;

@Model public interface TripleStoreSession extends StorageSession
{

	public void init(TripleStoreService tripleStoreServiceImpl, TripleStoreImpl storeImpl);

}
