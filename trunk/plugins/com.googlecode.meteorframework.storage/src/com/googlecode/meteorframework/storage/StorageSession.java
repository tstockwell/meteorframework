package com.googlecode.meteorframework.storage;

import java.util.List;

import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.Model;
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
@Model public interface StorageSession extends Resource 
{
	@Model public interface Constructor extends Service {
		public StorageSession create(StorageService connector);
	}

	public <T> T findByURI(Class<T> type, String uri)
		throws MeteorNotFoundException;
	
	public <T> List<T> list(Selector<T> query) 
		throws StorageException;
	
	/**
	 * Returns the StorageService associated with this session.
	 */
	public StorageService getConnector();
	
	


	/**
	 * Rollback any transaction in progress and release all resource associated 
	 * with this session.
	 * This method MUST be called by an application when it is finished with 
	 * a Session.
	 */
	public void close() throws StorageException;

	/**
	 * Removes a resource from the data store.
	 */
	public void delete(String uri) throws StorageException;

	/**
	 * Adds a previously unpersisted object to the data store.
	 * The given object must 
	 */
	public void persist(Object resource) throws StorageException;

    /**
     * Save all changes to persistent objects to the underlying system.
     */
    public void flush();

}
