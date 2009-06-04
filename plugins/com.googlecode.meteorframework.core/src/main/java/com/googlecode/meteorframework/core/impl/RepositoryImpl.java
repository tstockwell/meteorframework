package com.googlecode.meteorframework.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Repository;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;



@SuppressWarnings("unchecked")
@Decorator abstract public class RepositoryImpl 
implements Repository, Resource 
{
	@Decorates Scope _self;
	
	static HashMap<String, Resource> getResourcesByURIMap(ObjectImpl scopeImpl) {
		HashMap map= (HashMap) scopeImpl.getValue(MeteorNS.Repository.findResourceByURI);
		if (map == null) {
			map= new HashMap<String, Resource>();
			scopeImpl.setValue(MeteorNS.Repository.findResourceByURI, map);
		}
		return map;
	}
	static HashMap<String, Set<Resource>> getResourcesByTypeMap(ObjectImpl scopeImpl) {
		HashMap map= (HashMap) scopeImpl.getValue(MeteorNS.Repository.findResourcesByType);
		if (map == null) {
			map= new HashMap<String, Resource>();
			scopeImpl.setValue(MeteorNS.Repository.findResourcesByType, map);
		}
		return map;
	}
	
	
	
	@Override synchronized public void addResource(Resource resource) {
		ObjectImpl scopeImpl= ObjectImpl.getObjectImpl(_self);
		addResource(scopeImpl, getResourcesByURIMap(scopeImpl), getResourcesByTypeMap(scopeImpl), resource, new HashSet<String>());
	}
	static private void addResource(ObjectImpl scopeImpl, HashMap resourcesByURI, HashMap resourcesByType, Resource resource, HashSet<String> completedURIs) {
		
		// Check if resource has already been added in current invocation
		ObjectImpl resourceImpl= ObjectImpl.getObjectImpl(resource);
		String resourceURI= resourceImpl.internalGetURI();
		if (completedURIs.contains(resourceURI))
			return; 
		completedURIs.add(resourceURI);
		
		resourcesByURI.put(resourceURI, resource);
		
		// also track resources by type
		String typeURI= resourceImpl.getTypeURI();
		Set<Resource> resources= (Set<Resource>) resourcesByType.get(typeURI);
		if (resources == null) {
			resources= new HashSet<Resource>();
			resourcesByType.put(typeURI, resources);
		}
		resources.add(resource);
		
		/*
		 * A resource can only belong to one scope at a time (or the Resource.getScope()
		 * method needs to be change to return a set of scopes).
		 * Remove the resource from the old scope and set the scope on the resource to 
		 * this scope.
		 */
		resourceImpl._scope= scopeImpl;
		
		// Also add all reachable objects.
		// Adding an object to a repository is like using Hibernate to add objects 
		// to a database, all reachable objects are automagically added
		Collection<String> propertyURIs= new ArrayList(resourceImpl.keySet());
		for (Iterator iterator = propertyURIs.iterator(); iterator.hasNext();) {
			String propertyURI = (String) iterator.next();
			Object object= resourceImpl.getValue(propertyURI);
			ObjectImpl impl= ObjectImpl.getObjectImpl(object);
			if (impl != null) {
				addResource(scopeImpl, resourcesByURI, resourcesByType, impl, completedURIs);
			}
			else if (object instanceof Collection) {
				for (Iterator<?> i= new ArrayList((Collection)object).iterator(); i.hasNext();) {
					Object object2= i.next();
					impl= ObjectImpl.getObjectImpl(object2);
					if (impl != null) {
						addResource(scopeImpl, resourcesByURI, resourcesByType, impl, completedURIs);
					}
					else
						break;
				}
			}
			else if (object instanceof Map) {
				for (Iterator<?> i= new ArrayList(((Map)object).values()).iterator(); i.hasNext();) {
					Object object2= i.next();
					impl= ObjectImpl.getObjectImpl(object2);
					if (impl != null) {
						addResource(scopeImpl, resourcesByURI, resourcesByType, impl, completedURIs);
					}
					else
						break;
				}
				for (Iterator<?> i= new ArrayList(((Map)object).keySet()).iterator(); i.hasNext();) {
					Object object2= i.next();
					impl= ObjectImpl.getObjectImpl(object2);
					if (impl != null) {
						addResource(scopeImpl, resourcesByURI, resourcesByType, impl, completedURIs);
					}
					else
						break;
				}
			}
		}
	}
	
	@Override public Resource findResourceByURI(String p_uri) {
		return findResourceByURI(ObjectImpl.getObjectImpl(_self), p_uri);
	}

	@Override public Set<?> findResourcesByType(String typeURI) {
		Set<?> resources= findResourcesByType(ObjectImpl.getObjectImpl(_self), typeURI);
		if (resources == null)
			return Collections.EMPTY_SET;
		return resources;
	}

	@Override public <T> Set<T> findResourcesByType(Class<T> javaType)
	{
		Set<?> set= findResourcesByType(Meteor.getURIForClass(javaType));
		if (set.isEmpty())
			return (Set<T>) set;
		Set<T> set2= new HashSet<T>();
		for (Object object : set)
			set2.add((ObjectImpl.getObjectImpl(object)).internalCast(javaType));
		return set2;
	}

	@Override public <T> T findResourceByURI(String resourceURI, Class<T> baseType) {
		Resource resource= findResourceByURI(resourceURI);
		if (resource == null)
			return null;
		return ObjectImpl.getObjectImpl(resource).internalCast(baseType);
	}

	@Override public List<Resource> getAllResources() {
		return new ArrayList<Resource>(getResourcesByURIMap(ObjectImpl.getObjectImpl(_self)).values());
	}
	
	public static final Resource findResourceByURI(ObjectImpl scopeImpl, String uri)
	{
		Resource resource= (Resource)getResourcesByURIMap(scopeImpl).get(uri);
		if (resource == null) {
			Collection<?> nestedScopes= (Collection<?>) scopeImpl.getValue(MeteorNS.Node.children);
			if (nestedScopes != null) {
				for (Object nestedScope : nestedScopes) {
					resource= findResourceByURI(ObjectImpl.getObjectImpl(nestedScope), uri);
					if (resource != null)
						return resource;
				}
			}
		}
		return resource;
	}
	
	public static final Set<Resource> findResourcesByType(ObjectImpl scopeImpl, String uri)
	{
		HashSet<Resource> found= null;
		Set<Resource> resources= getResourcesByTypeMap(scopeImpl).get(uri);
		if (resources != null && !resources.isEmpty()) {
			found= new HashSet<Resource>();
			found.addAll(resources);
		}

		Collection<?> nestedScopes= (Collection<?>) scopeImpl.getValue(MeteorNS.Node.children);
		if (nestedScopes != null) {
			for (Object nestedScope : nestedScopes) {
				Set<Resource> nestedResources= findResourcesByType(ObjectImpl.getObjectImpl(nestedScope), uri);
				if (nestedResources != null && !nestedResources.isEmpty()) {
					if (found == null)
						found= new HashSet<Resource>();
					found.addAll(nestedResources);
				}
			}
		}

		if (found == null)
			return (Set<Resource>) Collections.EMPTY_SET;

		return found;
	}
	
	public static final void addResource(ObjectImpl scopeImpl, Resource resource)
	{
		addResource(scopeImpl, getResourcesByURIMap(scopeImpl), getResourcesByTypeMap(scopeImpl), resource, new HashSet<String>());
	}
	public static final <T> T findResourceByURI(ObjectImpl scopeImpl, String uri, Class<T> class1)
	{
		Resource resource= findResourceByURI(scopeImpl, uri);
		if (resource == null)
			return null;
		return ObjectImpl.getObjectImpl(resource).internalCast(class1);
	}
	public static final <T> T findResourceByURI(Scope scope, String nsURI, Class<T> class1)
	{
		return findResourceByURI(ObjectImpl.getObjectImpl(scope), nsURI, class1);
	}
	public static final <T> T findResourceByURI(Scope scope, String propertyURI)
	{
		return (T)findResourceByURI(ObjectImpl.getObjectImpl(scope), propertyURI);
	}
	public static final void addResource(Scope scope, Resource ns)
	{
		addResource(ObjectImpl.getObjectImpl(scope), ns);
	}
}
