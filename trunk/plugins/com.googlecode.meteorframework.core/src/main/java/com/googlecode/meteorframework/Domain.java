package com.googlecode.meteorframework;

import java.util.List;

import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.SameAs;


/**
 * Provides information about the Namespaces and Types available in the 
 * current scope.
 * 
 * @author ted stockwell
 */
@Model public interface Domain 
extends Service
{
	/**
	 * @return any nested domains
	 */
	@SameAs(MeteorNS.Node.children)
	List<Domain> getNestedDomains();
	
	/**
	 * @return The Meteor Type associated with a Java class
	 * 
	 * Creates a Meteor Type for the given java type is no associated 
	 * Type already exists in the current context.
	 */
	public <T> Type<T> findType(Class<T> javaType);
	
	/**
	 * @return The Meteor Namespace associated with a Java package
	 * 
	 * Creates a Meteor Namespace for the given java package is no associated 
	 * Namespace already exists in the current context.
	 */
	public Namespace findNamespace(Package javaPackage);
	
	

}
