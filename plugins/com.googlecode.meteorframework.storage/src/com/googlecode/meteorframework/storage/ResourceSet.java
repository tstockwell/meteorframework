package com.googlecode.meteorframework.storage;

import java.io.Serializable;

import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * An in-memory cache of Meteor Resources.
 * Normally associated with a StorageSession.
 * Can also be accessed directly when it is detached from a session.
 *  
 * @author ted stockwell
 */
@ModelElement
public interface ResourceSet extends Serializable {
	
	/**
	 * Returns resource with given uri or null if resource not found.
	 */
	<T> T findByURI(Class<T> type, String uri);
	
	void attach(Object resource);
	
	void detach(Object resource);
}
