package com.googlecode.meteorframework.storage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.After;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;

/**
 * An in-memory cache of Meteor Resources.
 * Tracks changes made to the resources.
 *  
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
public class ResourceCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, Resource> _resources = new LinkedHashMap<String, Resource>();
	private Map<String, ChangeTracker> _trackers = new LinkedHashMap<String, ChangeTracker>();
    	
	/** 
	 * Ordered list of resources to flush and purge. 
     * NEEDED to maintain the Order of the flushing for foreign key updates.
     */
	private ArrayList<String> _dirtyResources = new ArrayList<String>();
   
	@Decorator
	abstract static public class ChangeTracker 
	implements Resource 
	{
		@Decorates Resource _self;
		ResourceCache _resourceSet;
		
		@Override
		@After
		public <T> void setProperty(Property<T> property, T value, Object... parameters) {
			_resourceSet._dirtyResources.add(_self.getURI());
		}
		
		
		@Override
		@After
		public void setProperty(String propertyURI, Object value, Object... parameters) {
			_resourceSet._dirtyResources.add(_self.getURI());
		}
	}
    
    
	/**
	 * Returns resource with given uri or null if resource not found.
	 */
	public Resource findByURI(String uri)
	{
		return _resources.get(uri);
	}
	
	/**
	 * Adds given resource to this set.
	 * All resources that are 'reachable' from the given resource are also 
	 * attached to this set.
	 * If isDirty is true then all resources that are attached are also 
	 * marked as dirty.  
	 * Any reachable resources that are already attached are not 'reattached' 
	 * nor marked as dirty.  
	 */
	public void attach(Object object, boolean isDirty)
	{
		ArrayList<Resource> todo= new ArrayList<Resource>();
		todo.add((Resource)object);
		while (!todo.isEmpty())
		{
			Resource resource= todo.remove(0);
			if (internalAttach(resource, isDirty))
			{
				// find all reachable resources and add them to list
				for (Property<?> property : resource.getContainedProperties())
				{
					if (!property.isReference())
						continue;
					if (property.isTemporal())
						continue;

					Object value= resource.getProperty(property);
					if (property.isMany())
					{
						todo.addAll((Collection)value);
					}
					else
						todo.add((Resource)value);
				}
			}
		}
	}
	
	boolean internalAttach(Resource resource, boolean isDirty)
	{
		String uri= resource.getURI();
		if (_resources.get(uri) != null)
			return false; // already attached
		ChangeTracker tracker= resource.addDecorator(ChangeTracker.class);
		_resources.put(uri, ((Resource)resource));
		_trackers.put(uri, tracker);
		if (isDirty)
			_dirtyResources.add(uri);
		return true;
	}
	
	
	/**
	 * Removes given resource to this set.
	 */
	public void detach(Object object)
	{
		ArrayList<Resource> todo= new ArrayList<Resource>();
		todo.add((Resource)object);
		while (!todo.isEmpty())
		{
			Resource resource= todo.remove(0);
			if (internalDetach(resource))
			{
				// find all owned resources and detach them too
				for (Property<?> property : resource.getContainedProperties())
				{
					if (!property.isReference())
						continue;
					if (!property.isComposite())
						continue;

					Object value= resource.getProperty(property);
					if (property.isMany())
					{
						todo.addAll((Collection)value);
					}
					else
						todo.add((Resource)value);
				}
			}
		}
	}
	
	boolean internalDetach(Object object)
	{
		Resource resource= (Resource)object;
		String uri= resource.getURI();
		if (_resources.remove(uri) != null)
			return false;
		
		resource.removeDecorator(_trackers.remove(uri));
		_dirtyResources.remove(uri);
		return true;
	}
	
	public Set<Resource> getAllResources() {
		return new HashSet<Resource>(_resources.values());
	}
	
	/**
	 * Returns all dirty resource in this set
	 */
	public Set<Resource> getDirtyResources() {
		HashSet<Resource> set= new HashSet<Resource>();
		for (String uri: _dirtyResources)
			set.add(_resources.get(uri));
		return set;
	}

	public void clearDirtyList() {
		_dirtyResources.clear();
	}
    
   /** Remove all records from cache and update list. */
    public void purge() {
	   for (Resource resource : _resources.values())
	   {
		   detach(resource);
	   }
    }
    
   	public void destroy() {
        purge();
	}
    

    
}
