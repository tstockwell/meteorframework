package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.IsWriteOnce;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * An object that creates connections to a data store.
 * An application need only create a single StorageService to any data 
 * store and may reuse that connector throughout the application.
 * 
 * @author Ted Stockwell
 */
@Model public interface StorageService
extends Service
{
	public StorageSession openSession() 
		throws StorageException;
	
	public void close();

	public String getConnectionURL();
	
	/**
	 * A convenience method that opens a new session and 
	 * invokes the StorageTask.run(StorageSession) method.
	 * The session is automatically closed when the task 
	 * completes.
	 */
	public <T> T run(StorageTask<T> task);

	
}
