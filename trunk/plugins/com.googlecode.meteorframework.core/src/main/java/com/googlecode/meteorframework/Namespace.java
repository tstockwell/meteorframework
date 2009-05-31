package com.googlecode.meteorframework;

import java.util.Set;

import com.googlecode.meteorframework.annotation.InverseOf;
import com.googlecode.meteorframework.annotation.Model;



/**
 * Meteor Namespaces are similar to Java Packages.
 * Meteor uses the name 'Namespace' instead of Package' in order to avoid conflicts 
 * with the Java package keyword.
 * Like in Java where all classes belong to a package in Meteor all Types belong to a Namespace.
 * Meteor extends the function of namespaces by using namespaces to determine the 
 * order in which interceptors are invoked.
 * In Meteor namespaces may have dependencies on other namespaces.
 * Thus namespaces form a hierarchy of layers with namespaces with the fewest  
 * dependencies in the lowest layers and namespaces with the most dependencies in 
 * the higher layers.
 *    
 * Interceptors from namespaces which are dependent on other namespaces are invoked 
 * before the interceptors in those dependent namespaces.  In other words interceptors 
 * in higher layers are invoked before interceptors in lower layeres.
 * 
 * The ordering of namespaces is left to the repository implementation.
 * The default Meteor implementation is based on OSGi and all packages(namespaces) 
 * in a bundle will declare all packages(namespaces) from dependent bundles as 
 * dependencies.
 * Also, within a bundle a package may be explicitly marked as dependent on 
 * another package by creating a @Model package annotation in 
 * package-info.java like so...
 *		@Model({
 *			@Setting(property=MeteorNS.Namespace.dependencies, value=TestNames.NAMESPACE)})
 * 		package com.googlecode.meteorframework.test.extension;
 * 
 *  
 * @author Ted Stockwell 
 *  
 */
@Model public interface Namespace 
extends Resource, ModelElement, Comparable<Namespace> 
{
	@Model public interface Constructor extends Service {
		
		/**
		 * Creates a Namespace with the given URI.
		 */
		public Namespace create(String uri);

		/**
		 * Creates a Meteor Namespace based on a Java package.
		 */
		public abstract Namespace create(Package javaType);
	}
	
	/**
	 * The packages on which this package depends.
	 */
	@InverseOf(MeteorNS.Namespace.dependents) 
	public Set<Namespace> getDependencies();
	public void setDependencies(Set<Namespace> packages); 

	/**
	 * Packages which are dependent on this package.
	 */
	@InverseOf(MeteorNS.Namespace.dependencies) 
	public Set<Namespace> getDependents(); 
	public void setDependents(Set<Namespace> packages);
	

	public int compareTo(Namespace pakage);
	
	
	/**
	 * @return The types that belong to this namespace.
	 */
	@InverseOf(MeteorNS.Type.namespace) 
	public Set<Type<?>> getTypes();
	public void setTypes(Set<Type<?>> types);
}
