package com.googlecode.meteorframework.storage;

import java.util.Collection;
import java.util.List;

import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.IsWriteOnce;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.query.Selector;

/**
 * A transactional work session.
 * Use the net.sf.meteor.transaction.TransactionService to demarc 
 * transaction boundaries.
 * 
 * StorageSessions are threadsafe.
 *  
 * @author Ted Stockwell
 */
@ModelElement public interface StorageSession 
{
	public <T> T findByURI(Class<T> type, String uri)
		throws MeteorNotFoundException;
	
	public <T> List<T> list(Selector<T> query) 
		throws StorageException;
	
	/**
	 * Returns the StorageService associated with this session.
	 */
	public StorageService getStorageService();
	@IsWriteOnce public void setStorageService(StorageService service);
	
	
	public void attachResource(Object resource);
	public void attachResources(Collection<?> resources);
	public Collection<Resource> detachAllResources();

	/**
	 * Release all internal resources associated with this session.
	 * This method MUST be called by an application when it is finished with 
	 * a Session.
	 */
	public void close() throws StorageException;

	/**
	 * Removes a resource from the data store.
	 */
	public void delete(Object resource) throws StorageException;

	/**
	 * Adds a previously unpersisted object to the data store.
	 * The given object must 
	 */
	public void persist(Object resource) throws StorageException;

    /**
     * Persist all changes to the underlying storage system.
     * If the underlying storage system is transactional then all changes are 
     * persisted within a transaction.
     */
    public void flush() throws StorageException;

}
