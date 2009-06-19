package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.MeteorMethodNotImplementedException;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;


@SuppressWarnings("unchecked")

public class ObjectImpl implements Resource, Cloneable  {
	private static final long serialVersionUID = 1L;

	static final Object[] NO_ARGS = new Object[0];
	
	private static HashMap<String, List<String>> _typesCache= new HashMap<String, List<String>>();
	
	String _uri;
	ObjectImpl _scope;
	HashMap<Class, Object> _roles= new HashMap<Class, Object>();
	HashMap<String, Object> _attributes= new HashMap<String, Object>();
	String _typeURI;
	Class _containingModuleType; // Class of Module that created this resource, if any 

	private HashMap<Class, Object> _decorators= new HashMap<Class, Object>();
	
	private Map<String, Map<String, ParameterTypeSpecializer>> _methodSpecializersByType= null;
	private Map<String, Map<String, ParameterValueSpecializer>> _propertySpecializersByType= null;
	
	private static HashMap<String, HashMap<Class<?>, Enhancer>> __enhancers= new HashMap<String, HashMap<Class<?>, Enhancer>>(); 
	
	<T> T internalCast(Class<T> roleClass) {
		
		T role= findRole(roleClass);
		if (role != null)
			return role;
		
		return createRole(roleClass);
	}

	<T> T createRole(Class<T> roleClass) {
		
		// set default properties
		try {
			Type roleType= RepositoryImpl.findResourceByURI(_scope, Meteor.getURIForClass(roleClass), Type.class);
			if (roleType != null) 
				setDefaultProperties(roleType);
		}
		catch (MeteorMethodNotImplementedException t) {		
			/* setDefaultProperties barfs early in the Meteor boot process 
			 * because the getProperty method is not implemented yet.
			 */
		}
		
		try {
			
			Enhancer enhancer = new Enhancer();
		    enhancer.setCallback(new RoleInvocationHandler(_scope, this, roleClass));
		    enhancer.setClassLoader(Activator.getMeteorClassloader());
		    if (roleClass.isInterface()) {
			    enhancer.setInterfaces(new Class[] { MeteorProxy.class, Resource.class, roleClass });
		    }
		    else {
			    enhancer.setSuperclass(roleClass);
			    enhancer.setInterfaces(new Class[] { MeteorProxy.class, Resource.class });
		    }
		    
		    T role= (T)enhancer.create();
			
			_roles.put(roleClass, role);
			
			return role;
		}
		catch (Throwable t) {
			throw new MeteorException("Error creating role class "+roleClass, t);
		}
	}

	
	void setDefaultProperties(Type<?> roleType) {
		HashSet<Type> done= new HashSet<Type>();
		ArrayList<Type> todo= new ArrayList<Type>();
		todo.add(roleType);
		while (!todo.isEmpty()) {
			Type<?> type= todo.remove(0);
			if (done.contains(type))
				continue;					
			done.add(type);
			Class<?> class1= TypeAdvice.getJavaTypeFromTypeURI(ObjectImpl.getObjectImpl(type).internalGetURI());
			if (findRole(class1) != null)
				continue;
			for (Property<?> property : type.getDeclaredDefaultedProperties()) {
				Object value= property.getDefaultValue();
				setProperty(property, value);
			}
			todo.addAll(type.getSuperTypes());
		}
	}


	<T> T findRole(Class<T> roleClass) {
		// check for existing role
		// the fast way...
		T role= (T) _roles.get(roleClass);  
		if (role != null)
			return role;
		// the slow but accurate way...
		for (Class klass : _roles.keySet()) {
			if (roleClass.isAssignableFrom(klass))
				return (T)_roles.get(klass);
		}
		return null;
	}
	
	
	void addRole(Class<?> roleClass, Object role) {
		_roles.put(roleClass, role);
	}
	
	public Object getValue(String propertyURI) {
		return _attributes.get(propertyURI);
//		try {
//			Object value= null;
//			URI uri= URIPool.getURI(propertyURI);
//			RepositoryResult<Statement> repositoryResult= _scopeReference._sesameConnection.getStatements(this, uri, null, true);
//			if (repositoryResult.hasNext()) {
//				Statement statement= repositoryResult.next();
//				MeteorLiteral literal= (MeteorLiteral) statement.getObject();
//				value= literal.getNativeValue();
//			}
//			return value;
//		} 
//		catch (RepositoryException e) {
//			Logging.severe("Error getting attribute", e);
//			throw new MeteorException("Error getting attribute", e);
//		}
	}
	
	public void setValue(String propertyURI, Object value) {
		_attributes.put(propertyURI, value);
//		try {
//			URI uri= URIPool.getURI(propertyURI);
//			_scopeReference._sesameConnection.remove(this, uri, null);
//			MeteorLiteral literal= ValuePool.getValue(value, uri);
//			_scopeReference._sesameConnection.add(this, uri, literal);
//		} 
//		catch (RepositoryException e) {
//			Logging.severe("Error setting attribute", e);
//			throw new MeteorException("Error setting attribute", e);
//		}
	}
	public void setValue(ObjectImpl property, Object value) {
		setValue(property.internalGetURI(), value);
	}


	/**
	 * A resource is a collection of String/value pairs where the keys
	 * are URIs that denote the attribute.
	 * This methods returns the keys used to access the values.   
	 */
	public Set<String> keySet() {
		return _attributes.keySet();
//		try {
//			HashSet<String> keys= new HashSet<String>();
//			RepositoryResult<Statement> repositoryResult= _scopeReference._sesameConnection.getStatements(this, null, null, true);
//			while (repositoryResult.hasNext()) {
//				Statement statement= repositoryResult.next();
//				keys.add(statement.getPredicate().stringValue());
//			}
//			return keys;
//		} 
//		catch (RepositoryException e) {
//			Logging.severe("Error setting attribute", e);
//			throw new MeteorException("Error setting attribute", e);
//		}
	}
	
	@Override public ObjectImpl clone() {
		ObjectImpl object= null;
		try {
			object = (ObjectImpl)super.clone();
			object._roles= new HashMap<Class, Object>(_roles);
			object._attributes= new HashMap<String, Object>(_attributes);
			return object;
		} catch (CloneNotSupportedException e) { // not gonna happen
			e.printStackTrace();
		}
		return object;
	}
	
	@Override public String toString() {
		if (_uri != null)
			return _uri;
		return super.toString();
	}

	public String internalGetURI() {
		return _uri;
	}

	public void internalSetURI(String uri) {
		_uri = uri;
	}
	
	public Scope getScope() {
		return _scope.internalCast(Scope.class);
	}
	public String stringValue() {
		return _uri;
	}
	public String getTypeURI() {
		Object type= getValue(CoreNS.Resource.type);
		if (type != null) {
			ObjectImpl impl= ObjectImpl.getObjectImpl(type);
			if (impl != null)
				return impl._uri;
			return type.toString();
		}
		return _typeURI;
	}

	/**
	 * Returns the underlying Resource associated with a Role object. 
	 */
	public static final ObjectImpl getObjectImpl(Object target) {
		if (target == null)
			return null;
		ObjectImpl resource= null;
		if (target instanceof ObjectImpl) {
			resource= (ObjectImpl)target;
		}
		else if (target instanceof MeteorProxy) {
			resource= ((MeteorProxy)target).getObjectImpl();
		}
		return resource;
	}

	public void setTypeURI(String p_string) {
		_typeURI= p_string;		
	}


	public <T> T getDecorator(Class<T> decoratorClass)
	{
		// check for existing role
		// the fast way...
		T decorator= (T)_decorators.get(decoratorClass);  
		if (decorator == null) {
			// the slow but accurate way...
			for (Class klass : _decorators.keySet()) {
				if (decoratorClass.isAssignableFrom(klass))
					return (T)_decorators.get(klass);
			}
		}
		return decorator;
	}

	public <T> void addDecorator(Class<T> decoratorClass, T decorator)
	{
		_decorators.put(decoratorClass, decorator);		
	}
	
	@Override public void removeDecorator(Object decorator)
	{
		for (Map.Entry<Class, Object> entry : _decorators.entrySet()) {
			if (entry.getValue() == decorator) {
				_decorators.remove(entry.getKey());
				break;
			}
		}
	}
	
	@Override public <T> T addDecorator(Class<T> decoratorClass)
	{
		T decorator= getDecorator(decoratorClass);
		if (decorator != null)
			return decorator;
		
		decorator= createDecorator(decoratorClass);
		DecoratorAnnotationHandler handler= new DecoratorAnnotationHandler() {
			@Override protected void addMethodInterceptor(Interceptor interceptor, String decoratedTypeURI, String methodURI)
			{
				getMethodSpecializer(decoratedTypeURI, methodURI).addInterceptor(interceptor);
			}
			
			@Override protected void addPropertyInterceptor(Interceptor interceptor, String decoratedTypeURI, String propertyURI)
			{
				getPropertySpecializer(decoratedTypeURI, propertyURI).addInterceptor(decoratedTypeURI, propertyURI, interceptor);
			}
		};
		handler.initialize(_scope.internalCast(Scope.class), null, decoratorClass);
		handler.addBehavior();
		
		
		
		return decorator;
	}
	

	public ParameterTypeSpecializer getMethodSpecializer(String typeURI, String p_methodURI) {
		if (_methodSpecializersByType == null)
			_methodSpecializersByType= new HashMap<String, Map<String,ParameterTypeSpecializer>>();
		
		Map<String, ParameterTypeSpecializer> specializersByMethod= _methodSpecializersByType.get(typeURI);
		if (specializersByMethod == null) {
			specializersByMethod= new HashMap<String, ParameterTypeSpecializer>();
			_methodSpecializersByType.put(typeURI, specializersByMethod);
		}
		
		ParameterTypeSpecializer specializer= specializersByMethod.get(p_methodURI);
		if (specializer == null) {
			specializer= new ParameterTypeSpecializer();
			specializersByMethod.put(p_methodURI, specializer);
		}

		return specializer;
	}
	public ParameterTypeSpecializer findMethodSpecializer(String typeURI, String p_methodURI) {
		if (_methodSpecializersByType == null) 
			return null;
		
		Map<String, ParameterTypeSpecializer> specializersByMethod= _methodSpecializersByType.get(typeURI);
		if (specializersByMethod == null)
			return null;
		
		return specializersByMethod.get(p_methodURI);
	}
	public ParameterValueSpecializer getPropertySpecializer(String typeURI, String propertyURI) {
		if (_propertySpecializersByType == null)
			_propertySpecializersByType= new HashMap<String, Map<String,ParameterValueSpecializer>>();
		
		Map<String, ParameterValueSpecializer> specializersByMethod= _propertySpecializersByType.get(typeURI);
		if (specializersByMethod == null) {
			specializersByMethod= new HashMap<String, ParameterValueSpecializer>();
			_propertySpecializersByType.put(typeURI, specializersByMethod);
		}
		
		ParameterValueSpecializer specializer= specializersByMethod.get(propertyURI);
		if (specializer == null) {
			specializer= new ParameterValueSpecializer();
			specializersByMethod.put(propertyURI, specializer);
		}

		return specializer;
	}
	public ParameterValueSpecializer findPropertySpecializer(String typeURI, String propertyURI) {
		if (_propertySpecializersByType == null) 
			return null;
		
		Map<String, ParameterValueSpecializer> specializersByMethod= _propertySpecializersByType.get(typeURI);
		if (specializersByMethod == null)
			return null;
		
		return specializersByMethod.get(propertyURI);
	}

	public <T> T createDecorator(Class<T> decoratorClass)
	{
		T decorator= getDecorator(decoratorClass);
		if (decorator == null) {
			
			InvocationContextImpl ctx= new InvocationContextImpl();
			ctx._receiver= this;
			Meteor.pushInvocationContext(ctx);
			try {
				HashMap<Class<?>, Enhancer> enhancersForRepository= __enhancers.get(_scope.internalGetURI());
				if (enhancersForRepository == null) {
					enhancersForRepository= new HashMap<Class<?>, Enhancer>();
					__enhancers.put(_scope.internalGetURI(), enhancersForRepository);
				}
				Enhancer enhancer = enhancersForRepository.get(decoratorClass);
				if (enhancer == null) {
					enhancer = new Enhancer();
				    enhancer.setSuperclass(decoratorClass);
				    if (!Resource.class.isAssignableFrom(decoratorClass)) 
				    	enhancer.setInterfaces(new Class[] { Resource.class });
				    enhancer.setClassLoader(Activator.getMeteorClassloader());
				    //enhancer.setClassLoader(decoratorClass.getClassLoader());
				    enhancer.setCallback(new MethodInterceptor() {
						public Object intercept(Object theMixin, Method javaMethod, Object[] args, MethodProxy proxy)
						throws Throwable {
							if (Modifier.isAbstract(javaMethod.getModifiers())) {
//								String methodURI= Meteor.getURIForMethod(javaMethod);
//								ObjectImpl impl= ObjectImpl.this;
//								return impl._repository.invoke(methodURI, impl, args);
								throw new MeteorException("Invalid method call - decorators must use 'self' reference to call other methods." );
////								Object receiver= Meteor.getInvocationContext().getReceiver();
////								return javaMethod.invoke(receiver, args);
							}
							return proxy.invokeSuper(theMixin, args);
						}
				    });
				    enhancersForRepository.put(decoratorClass, enhancer);
				}
				decorator= (T)enhancer.create();
				
				DecoratorManager.injectDependencies(_scope.internalCast(Scope.class), decorator, this, ctx.getBindingContext());
				
				addDecorator(decoratorClass, decorator);
			}
			catch (Throwable t) {
				throw new MeteorException("Could not create instance of mixin type:"+decoratorClass, t);
			}
			finally {
				Meteor.popInvocationContext();
			}
			
		}
		return decorator;
	}
	

	
	
			
	public ObjectImpl(ObjectImpl scope, String typeURI, BindingContext bindingContext) {
		_scope= scope;		
		_typeURI= typeURI;
		if (_scope == null) {
			if (!typeURI.equals(CoreNS.Scope.TYPE))
				throw new MeteorException("Every resource must have a scope");
			_scope= this;
		}
		addRole(Resource.class, this);
		setValue(CoreNS.Resource.bindingContext, bindingContext);
		_uri= Meteor.PROTOCOL+UUID.randomUUID();
	}
	public ObjectImpl(Scope scope, String typeURI, BindingContext bindingContext) {
		this(ObjectImpl.getObjectImpl(scope), typeURI, bindingContext);
	}
	
	
	
	
	//////////////////////////////////
	//
	//	Methods from Resource interface.
	//	These methods merely call the Repository.invoke method so 
	//  that the methods are intercepted like normal Meteor methods.
	//
	
	@Override public boolean equals(Object object) {
		
		try {
			return (Boolean)MethodDispatcher.invoke(CoreNS.Resource.equals, this, new Object[] { object });
		}
		
		/*
		 * This error can happen early in the Meteor boot process 
		 */
		catch (MeteorMethodNotImplementedException x) {
			ObjectImpl impl= ObjectImpl.getObjectImpl(object);
			if (impl != null) 
				return internalGetURI().equals(impl.internalGetURI());
			return false;
		}
		//return this == ObjectImpl.getObjectImpl(p_obj);
	}
	
	@Override public int hashCode()
	{
		try {
			return (Integer)MethodDispatcher.invoke(CoreNS.Resource.hashCode, this, (Object[])null);
		}
		
		/*
		 * This error can happen early in the Meteor boot process 
		 */
		catch (MeteorMethodNotImplementedException x) {
			return internalGetURI().hashCode();
		}
	}

	@Override public <T> T castTo(Class<T> roleClass) {
		return (T)MethodDispatcher.invoke(CoreNS.Resource.castTo, this, new Object[] { roleClass });
	}

	@Override public Set<Resource> getRoles() {
		return getProperty(CoreNS.Resource.roles);
	}

	@Override public Set<Resource> getRoles(Type type) {
		return getProperty(CoreNS.Resource.roles, type);
	}

	@Override public <T> Set<T> getRoles(Class<T> javaType) {
		return getProperty(CoreNS.Resource.roles, javaType);
	}

	@Override public void setRoles(Set<Resource> roles) {
		setProperty(CoreNS.Resource.roles, roles);
	}

	@Override public String getURI() {
		return getProperty(CoreNS.Resource.uRI);
	}

	@Override public Set<Property<?>> getContainedProperties() {
		return (Set<Property<?>>)MethodDispatcher.invoke(CoreNS.Resource.getContainedProperties, this);
	}

	@Override public BindingContext getBindingContext() {
		return getProperty(CoreNS.Resource.bindingContext);
	}

	@Override public void addProperty(String propertyURI, Object value, Object... parameters)
	{
		// TODO Auto-generated method stub
		
	}

	@Override public void clearProperty(String propertyURI)
	{
		// TODO Auto-generated method stub
		
	}

	@Override public <T> T getProperty(Property<T> property, Object... parameters) {
		return getProperty(ObjectImpl.getObjectImpl(property).internalGetURI(), parameters);
	}

	@Override public <T> T getRole(Class<T> javaType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override public void setProperty(Property property, Object value, Object... parameters) {
		setProperty(ObjectImpl.getObjectImpl(property).internalGetURI(), value, parameters);
	}
	@Override public Type getType() {
		return (Type)MethodDispatcher.invoke(CoreNS.Resource.getProperty, this, new Object[] { CoreNS.Resource.type, new Object[0] });
	}
	@Override public Set<String> getSameAs()
	{
		return (Set<String>)MethodDispatcher.invoke(CoreNS.Resource.getProperty, this, new Object[] { CoreNS.Resource.sameAs, new Object[0] });
	}
	@Override public <T> T getProperty(String propertyURI, Object... arguments) {
		Object[] args= new Object[2];
		args[0]= propertyURI;
		args[1]= arguments;
		return (T)MethodDispatcher.invoke(CoreNS.Resource.getProperty, this, args);
	}
	@Override public String getDescription() {
		return (String)getProperty(CoreNS.Resource.description);
	}
	@Override public void setProperty(String propertyURI, Object value, Object... arguments) {
		Object[] args= new Object[3];
		args[0]= propertyURI;
		args[1]= value;
		args[2]= arguments;
		MethodDispatcher.invoke(CoreNS.Resource.setProperty, this, args);
	}

	@Override public void removeProperty(String propertyURI, Object value, Object... parameters) {
		Object[] args= new Object[3];
		args[0]= propertyURI;
		args[1]= value;
		args[2]= parameters;
		MethodDispatcher.invoke(CoreNS.Resource.removeProperty, this, args);
	}


	@Override public <T> Collection<T> getPropertyValues(String propertyURI)
	{
		Object[] args= new Object[1];
		args[0]= propertyURI;
		return (Collection<T>) MethodDispatcher.invoke(CoreNS.Resource.getPropertyValues, this, args);
	}
	
	@Override public boolean isInstanceOf(Class<?> javaType)
	{
		return (Boolean) MethodDispatcher.invoke(CoreNS.Resource.isInstanceOf, this, new Object[] { javaType });
	}
	
	@Override public boolean isInstanceOf(Type type)
	{
		return (Boolean) MethodDispatcher.invoke(CoreNS.Resource.isInstanceOf, this, new Object[] { type });
	}



	@Override public void setDescription(String p_description) {
		setProperty(CoreNS.Resource.description, p_description);
	}
	@Override public void setType(Type p_type) {
		setProperty(CoreNS.Resource.type, p_type);
	}
	@Override public void setType(Class javaType) {
		setProperty(CoreNS.Resource.type, javaType);
	}
	@Override public void setBindingContext(BindingContext bindingContext) {
		setProperty(CoreNS.Resource.bindingContext, bindingContext);
	}
	@Override public void setSameAs(Set<String> uris)
	{
		setProperty(CoreNS.Resource.sameAs, uris);
	}
	@Override public void setURI(String uri) {
		setProperty(CoreNS.Resource.uRI, uri);
	}
	@Override public String getLabel() {
		return getProperty(CoreNS.Resource.label);
	}
	@Override public void setLabel(String label) {
		setProperty(CoreNS.Resource.label, label);
	}
	@Override public void postConstruct() {
		MethodDispatcher.invoke(CoreNS.Resource.postConstruct, this, NO_ARGS);
	}


	/**
	 * Get a list of supertype URIs.
	 * This method is meant for low-level use and does its best to avoid as 
	 * much implementation infrastructure as possible.  
	 */
	public List<String> getAllMeteorTypeURIs() {
		String typeURI= getTypeURI();
		List<String> superTypes= _typesCache.get(typeURI);
		if (superTypes == null) {
			superTypes= new ArrayList<String>();
			HashSet<String> completedTypes= new HashSet<String>();
			ArrayList<String> todo= new ArrayList<String>();
			todo.add(typeURI);
			boolean foundAll= true;
			while (!todo.isEmpty()) 
			{
				String superTypeURI= todo.remove(0);
				if (completedTypes.contains(superTypeURI))
					continue;
				completedTypes.add(superTypeURI);
				superTypes.add(superTypeURI);

				Resource superType= RepositoryImpl.findResourceByURI(_scope, superTypeURI);
				if (superType != null) {
					Collection supers= (Collection) ObjectImpl.getObjectImpl(superType).getValue(CoreNS.Type.superTypes);
					if (supers != null) {
						for (Object type : supers) 
							todo.add(ObjectImpl.getObjectImpl(type).internalGetURI());
					}
				}
				else
					foundAll= false;
			}
			if (foundAll)
				_typesCache.put(typeURI, superTypes);
		}
		return superTypes;
	}
}
