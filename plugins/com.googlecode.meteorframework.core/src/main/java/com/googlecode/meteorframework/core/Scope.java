package com.googlecode.meteorframework.core;

import java.util.List;

import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.annotation.SameAs;



/**
 * Represents the runtime context in which objects operate.
 * A scope is a set of resources and services.
 * A scope is capable of creating new objects, looking up existing 
 * objects in the scope, and providing metadata about the types of objects 
 * in its domain.  
 * Meteor Resources created within a scope have access to the other resource 
 * and services within the scope.
 * 
 * Scopes may be nested.  Nested scopes are used to implement transient 
 * scopes that are based on a more global scope.  For instance, a custom 
 * scope may be created for handling servlet requests or application tasks.
 * When a scope is destroyed all the transient resources created just for 
 * that scope are discarded. 
 * 
 * A scope may be associated with a set of binding annotations.
 * Binding annotations affect the behavior of the scope and the resources 
 * within the scope's domain by causing method invocations to be bound to 
 * different sets of interceptors depending on the bindings.  
 * Nested scopes are usually created with just a single annotation but the systenm 
 * scope is often created with more than one binding annotation.  
 *   
 * When a Meteor application is started a system scope is created that 
 * contains metadata about the current system.  This system scope is 
 * associated with the @SystemScope scope annotation.
 * When starting a system the system scope may be assigned additional 
 * annotations by assigning annotation class names to the 
 * com.googlecode.meteorframework.scope system variable.
 * For example, the following command starts a system with 
 * a system scope of @Testing...
 * 		java -Dnet.sf.meteor.scope=com.googlecode.meteorframework.annotation.Testing ... 
 * 
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface Scope 
extends Provider, Repository, Domain 
{
	/**
	 * @return any nested scopes
	 */
	@SameAs(CoreNS.Node.children)
	List<Scope> getNestedScopes();
	
	/**
	 * Loads a Java class from any/all modules associated with this scope's domain.
	 * @throws ClassNotFoundException 
	 */
	<T> Class<T> loadClass(String driverClassName) throws ClassNotFoundException;

	/**
	 * Create a scope that nests this scope.
	 * 
	 * @param bindingAnnotation the scope annotation associated with this context.  May be null.
	 */
	Scope createScope(BindingType... bindingTypes);
	
}
