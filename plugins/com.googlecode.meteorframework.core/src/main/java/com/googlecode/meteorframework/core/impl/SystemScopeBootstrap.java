package com.googlecode.meteorframework.core.impl;

import java.util.ArrayList;
import java.util.HashSet;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;



/**
 * Creates the system scope and bootstraps the scope with basic metadata.
 * 
 * @author Ted Stockwell
 */
public class SystemScopeBootstrap {
	
	public static Scope getSystemScope() {
		return __systemScope;
	}
	
	static ObjectImpl __systemScopeImpl= new ObjectImpl((ObjectImpl)null, CoreNS.Scope.TYPE, null);
	static Scope __systemScope= __systemScopeImpl.internalCast(Scope.class);
	
	static private ArrayList<Class<? extends BindingType>> __cachedSystemBindings= new ArrayList<Class<? extends BindingType>>(); 

	static void registerSystemScopeBinding(Class<? extends BindingType> bindindType)
	{
			if (bindindType == null)
				return;
			if (!BindingType.class.isAssignableFrom(bindindType))
				return;
			String systemBindings= System.getProperty(Meteor.FACETS_SYSTEM_PROPERTY);
			if (systemBindings == null)
				return;
			if (systemBindings.contains(bindindType.getName())) {
				if (__cachedSystemBindings != null) {
					__cachedSystemBindings.add(bindindType);
				}
				else
					addSystemBinding(bindindType);
			}
	}

	private static void addSystemBinding(Class<? extends BindingType> bindindType)
	{
		try
		{
			BindingType annotation= getSystemScope().createInstance(bindindType);
			BindingContext binding= BindingContext.getBindingContext( annotation );

			BindingContext bindingContext= (BindingContext)__systemScopeImpl.getValue(CoreNS.Resource.bindingContext);
			if (bindingContext == null) {
				bindingContext= binding;
			}
			else
				bindingContext= bindingContext.union(binding);
			__systemScopeImpl.setValue(CoreNS.Resource.bindingContext, bindingContext);
		} 
		catch (Throwable e)
		{
			throw MeteorException.getMeteorException("Failed to creating binding", e);
		}
	}

	/**
	 * It is necessary to delay the creation of system bindings until after the 
	 * Meteor core bundle is loaded.
	 * This method is invoked by the Meteor bundle activator after loading metadata 
	 * in the Meteor bundle. 
	 */
	static void enableSystemBindings()
	{
		for (Class<? extends BindingType> bindindType : __cachedSystemBindings) {
			addSystemBinding(bindindType);
		}
		__cachedSystemBindings= null; // disable caching
	}
	
	
	static {
		
		ObjectImpl typeType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		typeType.internalSetURI(CoreNS.Type.TYPE);
		typeType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, typeType);
		
		ObjectImpl repositoryType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		repositoryType.internalSetURI(CoreNS.Repository.TYPE);
		repositoryType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, repositoryType);		
		
		ObjectImpl domainType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		domainType.internalSetURI(CoreNS.Domain.TYPE);
		domainType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, domainType);		
		
		ObjectImpl providerType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		providerType.internalSetURI(CoreNS.Provider.TYPE);
		providerType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, providerType);		
		
		ObjectImpl scopeType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		scopeType.internalSetURI(CoreNS.Scope.TYPE);
		scopeType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		HashSet<Object> superTypes= new HashSet<Object>();
		superTypes.add(providerType.internalCast(Type.class));
		superTypes.add(domainType.internalCast(Type.class));
		superTypes.add(repositoryType.internalCast(Type.class));
		scopeType.setValue(CoreNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, scopeType);		
		
		String objectTypeURI= Meteor.getURIForClass(Object.class);
		ObjectImpl objectType= new ObjectImpl(__systemScope, objectTypeURI, null);
		objectType.internalSetURI(objectTypeURI);
		objectType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, objectType);
		
		ObjectImpl resourceType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		resourceType.internalSetURI(CoreNS.Resource.TYPE);
		resourceType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(objectType.internalCast(Type.class));
		resourceType.setValue(CoreNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, resourceType);
		
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		typeType.setValue(CoreNS.Type.superTypes, superTypes);
		
		ObjectImpl methodType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		methodType.internalSetURI(CoreNS.Method.TYPE);
		methodType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		methodType.setValue(CoreNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, methodType);
		
		ObjectImpl propertyType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		propertyType.internalSetURI(CoreNS.Property.TYPE);
		propertyType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		propertyType.setValue(CoreNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, propertyType);
		
		// Namespace Type
		ObjectImpl namespaceType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		namespaceType.internalSetURI(CoreNS.Namespace.TYPE);
		namespaceType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		namespaceType.setValue(CoreNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, namespaceType);
		
		// Namespace.Constructor Type
		ObjectImpl namespaceConstructorType= new ObjectImpl(__systemScope, CoreNS.Type.TYPE, null);
		namespaceConstructorType.internalSetURI(CoreNS.Namespace.Constructor.TYPE);
		namespaceConstructorType.setValue(CoreNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		namespaceConstructorType.setValue(CoreNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, namespaceConstructorType);
		
		ObjectImpl meteorNamespace= new ObjectImpl(__systemScope, CoreNS.Namespace.TYPE, null);
		meteorNamespace.internalSetURI(CoreNS.NAMESPACE);
		meteorNamespace.setValue(CoreNS.Resource.type, namespaceType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, meteorNamespace);
		
		// com.googlecode.meteorframework.impl Namespace
		String implNamespace= "meteor:"+SystemScopeBootstrap.class.getPackage().getName();
		ObjectImpl meteorImplNamespace= new ObjectImpl(__systemScope, CoreNS.Namespace.TYPE, null);
		meteorImplNamespace.internalSetURI(implNamespace);
		meteorImplNamespace.setValue(CoreNS.Resource.type, namespaceType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, meteorImplNamespace);
		
		// com.googlecode.meteorframework.impl.query Namespace
		ObjectImpl meteorImplQueryNamespace= new ObjectImpl(__systemScope, CoreNS.Namespace.TYPE, null);
		meteorImplQueryNamespace.internalSetURI(implNamespace+".query");
		meteorImplQueryNamespace.setValue(CoreNS.Resource.type, namespaceType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, meteorImplQueryNamespace);
		
		ObjectImpl superTypesProperty= new ObjectImpl(__systemScope, CoreNS.Property.TYPE, null);
		superTypesProperty.internalSetURI(CoreNS.Type.superTypes);
		superTypesProperty.setValue(CoreNS.Property.many, true);
		superTypesProperty.setValue(CoreNS.Property.unique, true);
		superTypesProperty.setValue(CoreNS.Property.range, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, superTypesProperty);
		
		ObjectImpl subTypesProperty= new ObjectImpl(__systemScope, CoreNS.Property.TYPE, null);
		subTypesProperty.internalSetURI(CoreNS.Type.subTypes);
		subTypesProperty.setValue(CoreNS.Property.many, true);
		subTypesProperty.setValue(CoreNS.Property.unique, true);
		subTypesProperty.setValue(CoreNS.Property.range, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, subTypesProperty);
		
	}
}
