package com.googlecode.meteorframework;

import java.util.List;
import java.util.Set;

import com.googlecode.meteorframework.annotation.IsReadOnly;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.SameAs;



/**
 * A repository of Resources.
 * Provides methods for finding resources by URI or by Type.
 * 
 * @author Ted Stockwell
 */
@Model public interface Repository 
extends Service
{
	/**
	 * @return any nested repositories
	 */
	@SameAs(MeteorNS.Node.children)
	List<Repository> getNestedRepositories();
	
	/**
	 * Return all resources that exist in this context.
	 */
	@IsReadOnly List<Resource> getAllResources();

	/**
	 * Add a resource to this context.
	 */
	public void addResource(Resource resource);

	/**
	 * @return null if not found
	 */
	public Resource findResourceByURI(String resourceURI);
	
	/**
	 * A convenience method that finds the denoted Resource and casts it to the 
	 * given Java type. 
	 * @return null if not found
	 */
	public <T> T findResourceByURI(String resourceURI, Class<T> javaType);
	
	/**
	 * @return empty set if not found
	 */
	public Set<?> findResourcesByType(String typeURI);
	
	/**
	 * @return empty set if not found
	 */
	public <T> Set<T> findResourcesByType(Class<T> javaType);

}
