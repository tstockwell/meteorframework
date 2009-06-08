package com.googlecode.meteorframework.storage.triplestore.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.triplestore.SQLBuilder;
import com.googlecode.meteorframework.storage.triplestore.TripleStoreSession;

@Decorator public abstract class TripleStoreSessionImpl 
implements TripleStoreSession
{
	
	private TripleStoreImpl _tripleStoreImpl;
	private Connection _connection;
	private boolean _isClosed= false;
	
	private @Inject SQLBuilder.Constructor _sqlProvider;
	
	private @Decorates StorageSession _self;;
	
	public void init(TripleStoreServiceImpl connector, TripleStoreImpl storeImpl) {
		_tripleStoreImpl= storeImpl;
		_connection= _tripleStoreImpl.getJDBCConnection();
	}
	
	@Override public void flush()
	{
		_tripleStoreImpl.flush();
	}

	@Override synchronized public <T> T findByURI(Class<T> javaType, String uri)
	throws MeteorNotFoundException
	{
		checkClosed();
		_self.flush();
		Resource resource= _tripleStoreImpl.findResource(uri);
		if (resource == null)
			return null;
		return resource.castTo(javaType);
	}
	
	@Override synchronized public <T> List<T> list(Selector<T> criteria) 
	throws StorageException 
	{
		checkClosed();
		_self.flush();
		String sql= _sqlProvider.create(criteria).toString();
		List<Resource> rawList= _tripleStoreImpl.list(sql);
		Class<T> javaType= (Class<T>) criteria.getRange().getJavaType();
		ArrayList<T> list= new ArrayList<T>();
		for (Resource resource : rawList)
			list.add(resource.castTo(javaType));
		return list;
	}
	
//	@Override public void beginTransaction() throws StorageException {
//		try {
//			_connection.setAutoCommit(false);
//		} catch (SQLException e) {
//			throw new StorageException(e.getMessage(), e);
//		}
//		Meteor.proceed();		
//	}
//
//	@Override public void commitTransaction() throws StorageException {
//		try {
//			_connection.commit();
//		} catch (SQLException e) {
//			throw new StorageException(e.getMessage(), e);
//		}
//		Meteor.proceed();		
//	}
//	
//	@Override public void rollbackTransaction() throws StorageException {
//		try {
//			_connection.commit();
//		} catch (SQLException e) {
//			throw new StorageException(e.getMessage(), e);
//		}
//		Meteor.proceed();		
//	}

	@Override synchronized public void close() {
		checkClosed();
		_self.flush();
		
		try { _tripleStoreImpl.close(); } catch (Throwable t) { }
		try { _connection.close(); } catch (Throwable t) { }
		_isClosed= true;
		Meteor.proceed();		
	}

	/**
	 * Removes a resource from the data store.
	 */
	@Override synchronized public void delete(String uri) 
	throws StorageException 
	{
		checkClosed();
		_tripleStoreImpl.delete(uri);
	}

	/**
	 * Adds a previously unpersisted object to the data store.
	 */
	@Override synchronized public void persist(Object resource) 
	throws StorageException
	{
		checkClosed();
		if ((resource instanceof Resource) == false)
			throw new StorageException("Only objects that implement the "+Resource.class.getName()+" interface may be persisted");
		_tripleStoreImpl.persist((Resource)resource);
	}

	private void checkClosed()
	{
		if (_isClosed)
			throw new StorageException("Illegal method invocation, the storage session has already been closed.");
	}
	
}
