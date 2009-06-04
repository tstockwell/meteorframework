package com.googlecode.meteorframework.core.impl;

import java.util.ArrayList;
import java.util.HashSet;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.MeteorNS;
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
	
	static ObjectImpl __systemScopeImpl= new ObjectImpl((ObjectImpl)null, MeteorNS.Scope.TYPE, null);
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

			BindingContext bindingContext= (BindingContext)__systemScopeImpl.getValue(MeteorNS.Resource.facets);
			if (bindingContext == null) {
				bindingContext= binding;
			}
			else
				bindingContext= bindingContext.union(binding);
			__systemScopeImpl.setValue(MeteorNS.Resource.facets, bindingContext);
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
		
		ObjectImpl typeType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		typeType.internalSetURI(MeteorNS.Type.TYPE);
		typeType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, typeType);
		
		ObjectImpl repositoryType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		repositoryType.internalSetURI(MeteorNS.Repository.TYPE);
		repositoryType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, repositoryType);		
		
		ObjectImpl domainType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		domainType.internalSetURI(MeteorNS.Domain.TYPE);
		domainType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, domainType);		
		
		ObjectImpl providerType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		providerType.internalSetURI(MeteorNS.Provider.TYPE);
		providerType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, providerType);		
		
		ObjectImpl scopeType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		scopeType.internalSetURI(MeteorNS.Scope.TYPE);
		scopeType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		HashSet<Object> superTypes= new HashSet<Object>();
		superTypes.add(providerType.internalCast(Type.class));
		superTypes.add(domainType.internalCast(Type.class));
		superTypes.add(repositoryType.internalCast(Type.class));
		scopeType.setValue(MeteorNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, scopeType);		
		
		String objectTypeURI= Meteor.getURIForClass(Object.class);
		ObjectImpl objectType= new ObjectImpl(__systemScope, objectTypeURI, null);
		objectType.internalSetURI(objectTypeURI);
		objectType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, objectType);
		
		ObjectImpl resourceType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		resourceType.internalSetURI(MeteorNS.Resource.TYPE);
		resourceType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(objectType.internalCast(Type.class));
		resourceType.setValue(MeteorNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, resourceType);
		
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		typeType.setValue(MeteorNS.Type.superTypes, superTypes);
		
		ObjectImpl methodType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		methodType.internalSetURI(MeteorNS.Method.TYPE);
		methodType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		methodType.setValue(MeteorNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, methodType);
		
		ObjectImpl propertyType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		propertyType.internalSetURI(MeteorNS.Property.TYPE);
		propertyType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		propertyType.setValue(MeteorNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, propertyType);
		
		// Namespace Type
		ObjectImpl namespaceType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		namespaceType.internalSetURI(MeteorNS.Namespace.TYPE);
		namespaceType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		namespaceType.setValue(MeteorNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, namespaceType);
		
		// Namespace.Constructor Type
		ObjectImpl namespaceConstructorType= new ObjectImpl(__systemScope, MeteorNS.Type.TYPE, null);
		namespaceConstructorType.internalSetURI(MeteorNS.Namespace.Constructor.TYPE);
		namespaceConstructorType.setValue(MeteorNS.Resource.type, typeType.internalCast(Type.class));
		superTypes= new HashSet<Object>();
		superTypes.add(resourceType.internalCast(Type.class));
		namespaceConstructorType.setValue(MeteorNS.Type.superTypes, superTypes);
		RepositoryImpl.addResource(__systemScope, namespaceConstructorType);
		
		ObjectImpl meteorNamespace= new ObjectImpl(__systemScope, MeteorNS.Namespace.TYPE, null);
		meteorNamespace.internalSetURI(MeteorNS.NAMESPACE);
		meteorNamespace.setValue(MeteorNS.Resource.type, namespaceType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, meteorNamespace);
		
		// com.googlecode.meteorframework.impl Namespace
		ObjectImpl meteorImplNamespace= new ObjectImpl(__systemScope, MeteorNS.Namespace.TYPE, null);
		meteorImplNamespace.internalSetURI("meteor:com.googlecode.meteorframework.impl");
		meteorImplNamespace.setValue(MeteorNS.Resource.type, namespaceType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, meteorImplNamespace);
		
		// com.googlecode.meteorframework.impl.query Namespace
		ObjectImpl meteorImplQueryNamespace= new ObjectImpl(__systemScope, MeteorNS.Namespace.TYPE, null);
		meteorImplQueryNamespace.internalSetURI("meteor:com.googlecode.meteorframework.impl.query");
		meteorImplQueryNamespace.setValue(MeteorNS.Resource.type, namespaceType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, meteorImplQueryNamespace);
		
		ObjectImpl superTypesProperty= new ObjectImpl(__systemScope, MeteorNS.Property.TYPE, null);
		superTypesProperty.internalSetURI(MeteorNS.Type.superTypes);
		superTypesProperty.setValue(MeteorNS.Property.many, true);
		superTypesProperty.setValue(MeteorNS.Property.unique, true);
		superTypesProperty.setValue(MeteorNS.Property.range, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, superTypesProperty);
		
		ObjectImpl subTypesProperty= new ObjectImpl(__systemScope, MeteorNS.Property.TYPE, null);
		subTypesProperty.internalSetURI(MeteorNS.Type.subTypes);
		subTypesProperty.setValue(MeteorNS.Property.many, true);
		subTypesProperty.setValue(MeteorNS.Property.unique, true);
		subTypesProperty.setValue(MeteorNS.Property.range, typeType.internalCast(Type.class));
		RepositoryImpl.addResource(__systemScope, subTypesProperty);
		
	}
}
