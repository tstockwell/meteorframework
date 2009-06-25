package com.googlecode.meteorframework.storage.appengine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.After;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.core.utils.ConversionService;
import com.googlecode.meteorframework.storage.ResourceSet;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.StorageNS;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageService;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageSession;

@Decorator public abstract class AppengineStorageSessionImpl 
implements AppengineStorageSession, Resource
{
	
	private @Decorates AppengineStorageSession _self;
	private @Inject ConversionService _conversionService;
	private @Inject Scope _scope; 
	
	private boolean _isClosed= false;
	private AppengineStorageService _storageService;
	private DatastoreService _datastoreService;
	
	@Override 
	public void postConstruct() {
		_storageService= ((Resource)_self.getStorageService()).castTo(AppengineStorageService.class);
		_datastoreService= _storageService.getDatastoreService();
		
		ResourceSet resourceSet= _self.getResourceSet();
		if (resourceSet == null)
			_self.attachResourceSet(_scope.createInstance(ResourceSet.class));
	}
	
	@Override
	public void attachResourceSet(ResourceSet resourceSet) {
		ResourceSet currentResourceSet= _self.getResourceSet();
		if (currentResourceSet != null)
			currentResourceSet.setStorageSession(null);
		((Resource)_self).setProperty(StorageNS.StorageSession.resourceSet, resourceSet);
	}
	
	@Override public void flush()
	{
		for (Transaction transaction : _datastoreService.getActiveTransactions())
		{
			transaction.commit();
		}
	}

	@Override synchronized public <T> T findByURI(Class<T> javaType, String uri)
	throws MeteorNotFoundException
	{
		try 
		{
			checkClosed();
			
			ResourceSet resourceSet= _self.get

			Key key= KeyFactory.stringToKey(uri);
			Entity entity= _datastoreService.get(key);
			Resource resource= _conversionService.convert(entity, Resource.class);
			return resource.castTo(javaType);
		} 
		catch (EntityNotFoundException e) 
		{
			throw new MeteorNotFoundException(uri);
		}
	}
	
	@Override synchronized public <T> List<T> list(Selector<T> criteria) 
	throws StorageException 
	{
		checkClosed();
		_self.flush();
		Query query= _conversionService.convert(criteria, Query.class);
		Class<T> javaType= (Class<T>) criteria.getRange().getJavaType();
		ArrayList<T> list= new ArrayList<T>();
		for (Entity entity : _datastoreService.prepare(query).asIterable())
		{
			Resource resource= _conversionService.convert(entity, Resource.class);
			list.add(resource.castTo(javaType));
		}
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

	@After @Override synchronized public void close() {
		checkClosed();
		_self.flush();
		
		Transaction transaction= _datastoreService.getCurrentTransaction();
		if (transaction != null)
			transaction.rollback();
	}

	/**
	 * Removes a resource from the data store.
	 */
	@Override synchronized public void delete(String uri) 
	throws StorageException 
	{
		checkClosed();
		_datastoreService.delete(KeyFactory.stringToKey(uri));
	}

	/**
	 * Adds a previously unpersisted object to the data store.
	 */
	synchronized public void persist(Resource resource) 
	throws StorageException
	{
		checkClosed();
		HashSet<String> completedURIs= new HashSet<String>();
		persist(resource, completedURIs);
	}
	
	private void persist(Resource resource, HashSet<String> completedURIs) 
	{
		
		String resourceURI= resource.getURI();
		completedURIs.add(resourceURI);
		
		Set<Property<?>> properties= resource.getContainedProperties();
		for (Property<?> property : properties) 
		{
			if (property.isTemporal())
				continue;
			
			Object value= resource.getProperty(property);
			if (property.isMany()) {
				if (property.isOrdered()) {
					int i= 0;
					for (Object val : (Collection<?>)value) 
						persistPropertyValue(resourceURI, property, i++, val, statements, completedURIs);
				}
				else if (property.isIndexed()) {
					for (Map.Entry<?, ?> entry : ((Map<?,?>)value).entrySet())  
						persistPropertyValue(resourceURI, property, entry.getKey(), entry.getValue(), statements, completedURIs);
				}
				else {
					for (Object val : (Collection<?>)value) 
						persistPropertyValue(resourceURI, property, null, val, statements, completedURIs);
				}
			}
			else 
				persistPropertyValue(resourceURI, property, null, value, statements, completedURIs);
		}
		
		
		Entity entity= _conversionService.convert(resource, Entity.class);
		_datastoreService.put(entity);
	}

	private void checkClosed()
	{
		if (_isClosed)
			throw new StorageException("Illegal method invocation, the storage session has already been closed.");
	}
	
}
