package com.googlecode.meteorframework.storage.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.After;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.storage.ResourceSet;

@Decorator
public abstract class ResourceSetImpl implements ResourceSet {

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
		ResourceSetImpl _resourceSet;
		
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
    
    
	@Override 
	public <T> T findByURI(Class<T> type, String uri)
	{
		Resource resource= _resources.get(uri);
		if (resource == null)
			return null;
		return resource.castTo(type);
	}
	
	@Override 
	public void attach(Object object)
	{
		Resource resource= (Resource)object;
		String uri= resource.getURI();
		if (_resources.get(uri) == null)
		{
			ChangeTracker tracker= resource.addDecorator(ChangeTracker.class);
			_resources.put(uri, ((Resource)resource));
			_trackers.put(uri, tracker);
		}
	}
	
	@Override 
	public void detach(Object object)
	{
		Resource resource= (Resource)object;
		String uri= resource.getURI();
		if (_resources.remove(uri) != null)
		{
			resource.removeDecorator(_trackers.remove(uri));
			_dirtyResources.remove(uri);
		}
	}
	
	@Override
	public Set<Resource> getAllResources() {
		return new HashSet<Resource>(_resources.values());
	}
	
	@Override
	public Set<Resource> getDirtyResources() {
		HashSet<Resource> set= new HashSet<Resource>();
		for (String uri: _dirtyResources)
			set.add(_resources.get(uri));
		return set;
	}

	@Override
	public void clearDirtyList() {
		_dirtyResources.clear();
	}
    
   /** Remove all records from cache and update list. */
   @Override
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
