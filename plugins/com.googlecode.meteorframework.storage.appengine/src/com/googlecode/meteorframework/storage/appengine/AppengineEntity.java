package com.googlecode.meteorframework.storage.appengine;

import com.google.appengine.api.datastore.Key;
import com.googlecode.meteorframework.core.annotation.IsTemporal;
import com.googlecode.meteorframework.core.annotation.IsWriteOnce;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * A resource that is persisted in the GAE datastore.
 * @author Ted Stockwell
 */
@ModelElement
public interface AppengineEntity {
	@IsTemporal
	Key getKey();
	@IsWriteOnce
	void setKey(Key key);
}
