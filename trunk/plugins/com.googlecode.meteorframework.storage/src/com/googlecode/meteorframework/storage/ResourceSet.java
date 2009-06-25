package com.googlecode.meteorframework.storage;

import java.io.Serializable;
import java.util.Set;

import com.googlecode.meteorframework.core.Resource;
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
	
	/**
	 * Adds given resource to this set.
	 */
	void attach(Object resource);
	
	/**
	 * Removes given resource to this set.
	 */
	void detach(Object resource);
	
	/**
	 * Returns all dirty resource in this set
	 */
	Set<Resource> getDirtyResources();
	
	/**
	 * Returns all resource in this set
	 */
	Set<Resource> getAllResources();
	
	/**
	 * Removes all resources from this set and releases any internal resources
	 */
	void close();
	
	StorageSession getStorageSession();
	void setStorageSession(StorageSession session);
	
	void clearDirtyList();
	
	/** Remove all records from cache and update list. */
    void purge();
	
	
}
