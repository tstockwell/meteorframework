package com.googlecode.meteorframework.core;

import java.util.List;

import com.googlecode.meteorframework.core.annotation.IsMethod;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.annotation.SameAs;


/**
 * Creates or provides object instances and performs dependency injection.
 *  
 * @author Ted Stockwell
 */
@Model public interface Provider 
extends Service
{
	/**
	 * @return any nested providers
	 */
	@SameAs(MeteorNS.Node.children)
	public List<Provider> getNestedProviders();
	
	/**
	 * Injects dependencies into an object created outside of Meteor.
	 * Will inject values into fields and will call initializer methods.
	 * 
	 * @param bindings optional binding annotations
	 *    
	 * @throws MeteorException if an instance cannot be instantiated for an 
	 * injected field. 
	 */ 
	public void injectMembers(Object o, BindingType... bindings);
	 
	/**
	 * Provides an instance of {@code T}.
	 * If an appropriate instance of T is available then this method
	 * will return the available instance otherwise this method 
	 * will create a new instance of T by calling the createInstance method.
	 * Exactly what instance is returned is determined by the given bindings. 
	 * The returned instance is already injected with any dependencies. 
	 * Never returns {@code null}.
	 * 
	 * The Provider.getInstance method is also used during dependency 
	 * injection.
	 * For example, this injection..
	 * 		@Inject @Testing Configuration _config;
	 * ...results in this invocation of the Provider.getInstance method... 
	 * 		provider.getInstance(Configuration.class, provider.getInstance(Testing.class));
	 * 
	 * @throws MeteorException if an instance of the given type cannot be 
	 * instantiated. 
	 */
	@IsMethod public <T> T getInstance(TypeLiteral<T> type, BindingType... bindings);
	@IsMethod public <T> T getInstance(Class<T> javaType, BindingType... bindings);
	
	/**
	 *	Creates a new instance of T.
	 *	Unlike getInstance, the create method is strictly used to create new 
	 *  instances.   
	 */
	public <T> T createInstance(TypeLiteral<T> javaType, BindingType... bindings);
	public <T> T createInstance(Class<T> javaType, BindingType... bindings);
	
	/**
	 * Adds an instance to this Provider's internal cache of objects.
	 * The given instance will be returned from future calls to the getInstance method 
	 * where the given type and bindings match the type and bindings passed in the 
	 * call to putInstance. 
	 * 
	 * This method is used to make objects globally available within a scope.
	 * For instance, if a new scope is created to handle a servlet request then the 
	 * request object can be made available to other objects within the scope 
	 * using the setInstance method.
	 * For instance, the following code will cause the current servlet request to be 
	 * return from all calls to Provider.getInstance(ServletRequest.class) and also 
	 * cause the current request instance to be injected into all objects 
	 * that @Inject a ServletRequest object.... 
	 * 
	 * 		Scope systemScope= Meteor.getSystemScope();
	 * 		Scope requestScope= systemScope.createScope(systemScope.getInstance(RequestScope.class));
	 * 		requestScope.setInstance(servletRequest, ServletRequest.class);
	 * 
	 * later, the servletRequest object will be injected into injection points like the following...
	 * 		@Inject ServletRequest _request;
	 * 
	 */
	@IsMethod public <T> void putInstance(T instance, TypeLiteral<T> type, BindingType... bindings);
	@IsMethod public <T> void putInstance(T instance, Class<T> javaType, BindingType... bindings);
	
	
	/**
	 *	Finds an appropriate instance of T in this Provider's internal cache.
	 *	If no instance is currently available then null is returned.
	 */
	public <T> T findInstance(TypeLiteral<T> javaType, BindingType... bindings);
	public <T> T findInstance(Class<T> javaType, BindingType... bindings);
	
}
