package com.googlecode.meteorframework.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * A binding represents some client-visible semantic of an API 
 * implementation that is satisfied by some implementations of the API and not by 
 * others. 
 * For example, we could introduce bindings representing synchronicity 
 * and asynchronicity.  Or bindings representing production code and test code.
 * Or bindings that represent different customers.
 * 
 * For instance, here is how bindings might be used to 
 * create synchronous and asynchronous implementations of a base type...
 * 
 * <code>
 * @Binding(Synchronous.class)
 * class SynchronousPaymentProcessor implements PaymentProcessor {
 * 		 ...
 * }
 * </code>
 * <code>
 * @Binding(Asynchronous.class)
 * class AsynchronousPaymentProcessor implements PaymentProcessor {
 * 		...
 * }
 * </code>
 * 
 * Finally, bindings are applied to injection points to distinguish which 
 * implementation is required by the client. 
 * For example, when Meteor encounters the following injected field, an instance 
 * of SynchronousPaymentProcessor will be injected:
 * <code>
 *		@Inject @Binding(Synchronous.class) PaymentProcessor paymentProcessor;
 * </code>
 * 
 * But in this case, an instance of AsynchronousPaymentProcessor will be injected:
 * <code>
 * 		@Inject @Binding(Asynchronous.class) PaymentProcessor paymentProcessor;
 * </code>
 * 
 * Since BindingTypes are required a priori to implement Meteor-style method 
 * dispatching BindingTypes are regular Java class not Meteor object instances. 
 *  
 * 
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
@ModelElement public class BindingType
implements Resource
{
	private HashMap<String, Object> _properties= new HashMap<String, Object>();
	
	public BindingType() {
		Scope scope= Meteor.getSystemScope();
		setProperty(CoreNS.Resource.scope, scope);
		setProperty(CoreNS.Resource.bindingContext, ((Resource)scope).getBindingContext());
	}
	

	@Override
	public <T> T addDecorator(Class<T> decoratorClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProperty(String propertyURI, Object value, Object... parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T castTo(Class<T> javaType) {
		return (T) this;
	}

	@Override
	public void clearProperty(String propertyURI) {
		_properties.remove(propertyURI);
		
	}

	@Override
	public BindingContext getBindingContext() {
		return getProperty(CoreNS.Resource.bindingContext);
	}

	@Override
	public Set<Property<?>> getContainedProperties() {
		Scope scope= getScope();
		Set set= new HashSet();
		for (String propertyURI : _properties.keySet())
			set.add(scope.findResourceByURI(propertyURI));
		return set;
	}

	@Override
	public String getDescription() {
		return getProperty(CoreNS.Resource.description);
	}

	@Override
	public String getLabel() {
		return getProperty(CoreNS.Resource.label);
	}

	@Override
	public <T> T getProperty(String propertyURI, Object... parameters) {
		return (T) _properties.get(propertyURI);
	}

	@Override
	public <T> T getProperty(Property<T> property, Object... parameters) {
		return getProperty(property.getURI(), parameters);
	}

	@Override
	public <T> Collection<T> getPropertyValues(String propertyURI) {
		return (Collection<T>) _properties.values();
	}

	@Override
	public <T> T getRole(Class<T> javaType) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T getRole(Type<T> javaType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeRole(Object roleObject) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T addRole(Class<T> roleClass) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Resource getActor() {
		return getProperty(CoreNS.Resource.actor);
	}

	@Override
	public Set<Resource> getRoles() {
		return getProperty(CoreNS.Resource.roles);
	}

	@Override
	public Set<Resource> getRoles(Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Set<T> getRoles(Class<T> javaType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getSameAs() {
		return getProperty(CoreNS.Resource.sameAs);
	}

	@Override
	public Scope getScope() {
		return getProperty(CoreNS.Resource.scope);
	}

	@Override
	public Type<?> getType() {
		return getProperty(CoreNS.Resource.type);
	}

	@Override
	public String getURI() {
		return getProperty(CoreNS.Resource.uRI);
	}

	@Override
	public boolean isInstanceOf(Class<?> javaType) {
		return getClass().isAssignableFrom(javaType);
	}

	@Override
	public boolean isInstanceOf(Type type) {
		return getClass().isAssignableFrom(type.getJavaType());
	}

	@Override
	public void postConstruct() {
		// do nothing
	}

	@Override
	public void removeDecorator(Object decorator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeProperty(String propertyURI, Object value, Object... parameters) {
		_properties.remove(propertyURI);
		
	}

	@Override
	public void setBindingContext(BindingContext bindingContext) {
		setProperty(CoreNS.Resource.bindingContext, bindingContext);
	}

	@Override
	public void setDescription(String p_description) {
		setProperty(CoreNS.Resource.description, p_description);
	}

	@Override
	public void setLabel(String label) {
		setProperty(CoreNS.Resource.label, label);
	}

	@Override
	public void setProperty(String propertyURI, Object value, Object... parameters) {
		_properties.put(propertyURI, value);
	}

	@Override
	public void setProperty(Property property, Object value, Object... parameters) {
		_properties.put(property.getURI(), value);
	}

	@Override
	public void setRoles(Set<Resource> roles) {
		setProperty(CoreNS.Resource.roles, roles);
	}

	@Override
	public void setSameAs(Set<String> uris) {
		setProperty(CoreNS.Resource.sameAs, uris);
	}

	@Override
	public void setType(Type p_type) {
		setProperty(CoreNS.Resource.type, p_type);
	}

	@Override
	public void setType(Class class1) {
		setProperty(CoreNS.Resource.type, getScope().findType(class1));
	}

	@Override
	public void setURI(String uri) {
		setProperty(CoreNS.Resource.uRI, uri);
	}
}
