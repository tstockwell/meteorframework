package com.googlecode.meteorframework.core;

import java.util.Collection;
import java.util.Set;

import com.googlecode.meteorframework.core.annotation.ElementType;
import com.googlecode.meteorframework.core.annotation.IsFunction;
import com.googlecode.meteorframework.core.annotation.IsTemporal;
import com.googlecode.meteorframework.core.annotation.ModelElement;



/**
 * Basic functionality of any Model object.
 * 
 * This interface defines methods and properties inherited by all Meteor objects.
 * The Meteor equivalent of java.lang.Object.
 * All objects created by Meteor will implement this interface whether the 
 * object explicitly inherits the Resource interface or not. 
 *  
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
@ModelElement 
public interface Resource {
		
	/**
	 * Returns the scope from which this object was created.
	 */
	public Scope getScope();
	
	/**
	 * Every resource remembers the binding context in which it was created.
	 * Every method call or property access made through a resource 
	 * has the resource's binding context added to the current binding context.
	 * 
	 * This property enables some interesting capabilities.
	 * For instance, it would be possible to implement a storage facility 
	 * such that test data could be inserted into a production database 
	 * and the test data will only be visible to applications that are 
	 * running in 'test mode' because only data that matches the current 
	 * binding context would be included in selects. 
	 */
	@IsTemporal
	public BindingContext getBindingContext();
	public void setBindingContext(BindingContext bindingContext);
	
	/**
	 * Get this object's URI.
	 */
	public String getURI();
	public void setURI(String uri);
	

	/**
	 * Cast this object to another Role or Extension type.
	 */
	public <T> T castTo(Class<T> javaType);
	
	/**
	 * Invoked after a resource is constructed by Meteor to give the 
	 * new instance a chance to do some initialization.
	 */
	public void postConstruct();
	
	@IsFunction 
	public boolean isInstanceOf(Class<?> javaType);
	
	@IsFunction 
	public boolean isInstanceOf(Type type);
		
	@IsFunction 
	public void setProperty(String propertyURI, Object value, Object...parameters);
	
	@IsFunction 
	public <T> void setProperty(Property<T> property, T value, Object...parameters);
	
	/**
	 * A convenient method that looks up the denoted property in the current 
	 * scope and then calls getProperty(Property, Object...)
	 */
	@IsFunction 
	public  <T> T getProperty(String propertyURI, Object...parameters);
	
	@IsFunction 
	public <T> T getProperty(Property<T> property, Object...parameters);
	
	/**
	 * This method returns all the values stored in this resource for the given property.
	 * If this property is single-values then a Collection with only one element is returned.
	 * If this property is indexed or ordered then a collection of the values, 
	 * in no particular order, is returned.  
	 */
	@IsFunction 
	public <T> Collection<T> getPropertyValues(String propertyURI);
	
	/**
	 * Adds a value to a multivalued property.
	 * Since the setProperty MUST also accept individual values for multivalued properties 
	 * this method is really just a convenience.   
	 */
	@IsFunction 
	public void addProperty(String propertyURI, Object value, Object...parameters);
	
	@ElementType(CoreNS.Function.TYPE) 
	public void removeProperty(String propertyURI, Object value, Object...parameters);
	
	@IsFunction 
	public void clearProperty(String propertyURI);
	
	public String getDescription();
	public void setDescription(String p_description);
	
	public String getLabel();
	public void setLabel(String label);
	
	/**
	 * The sameAs property denotes other URI references that reference this same resource.
	 * That is, the resources referenced by the sameAs property have the same "identity".
	 * 
	 * This property is meant to be equivalent to the OWL:sameAs property.
	 */
	public Set<String> getSameAs();
	public void setSameAs(Set<String> uris);
	
	
	/**
	 * Denotes a resource's base type.
	 * The base type determines what roles an object can eventually play.
	 */
	public Type<?> getType();
	public void setType(Type p_type);
	public void setType(Class class1);
	
	/**
	 * Returns all the roles played by this object.
	 * @return an immutable set of objects
	 */
	public Set<Resource> getRoles();
	public void setRoles(Set<Resource> roles);
	
	/**
	 * Returns all the roles of a given Type played by this object.
	 */
	@IsFunction public <T> Set<T> getRoles(Type<T> type);
	@IsFunction public <T> Set<T> getRoles(Class<T> javaType);
	/**
	 * A convenience method that returns the first available role of the given type.
	 */
	@IsFunction public <T> T getRole(Class<T> javaType);
	@IsFunction public <T> T getRole(Type<T> javaType);
	
	/**
	 * If this object is a role object then this property denotes the 
	 * object that is playing this role.
	 * @return null if this object is not a role object
	 */
	public Resource getActor();
	
	public <T> T addRole(Class<T> roleClass);
	public void removeRole(Object roleObject);
	
	public int hashCode();
	public boolean equals(Object obj);
	/**
	 * The default implement of this method returns a representation 
	 * of this resource in N3 triple format.
	 */
	public String toString();
	
	@IsFunction  public Set<Property<?>> getContainedProperties();

	/**
	 * Creates a decorator that intercepts methods calls on this object and 
	 * applies the decorator to this object only.
	 * The given class may be abstract and must have a noarg constructor.
	 */
	public <T> T addDecorator(Class<T> decoratorClass);
	
	/**
	 * Removes a decorator that was returned from the addDecorator method.
	 * @param decorator
	 */
	void removeDecorator(Object decorator);
}
