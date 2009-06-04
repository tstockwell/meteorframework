package com.googlecode.meteorframework.core;

import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class MeteorNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.core";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Message {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Message";

	}

	public static interface Receipt {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Receipt";

		// methods
		public static final String cancel = "meteor:com.googlecode.meteorframework.core.Receipt.cancel";
		public static final String getResult = "meteor:com.googlecode.meteorframework.core.Receipt.getResult";
		public static final String isCancelled = "meteor:com.googlecode.meteorframework.core.Receipt.isCancelled";
		public static final String isDone = "meteor:com.googlecode.meteorframework.core.Receipt.isDone";

	}

	public static interface Domain {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Domain";

		// properties
		public static final String nestedDomains = "meteor:com.googlecode.meteorframework.core.Domain.nestedDomains";

		// methods
		public static final String findNamespace = "meteor:com.googlecode.meteorframework.core.Domain.findNamespace";
		public static final String findType = "meteor:com.googlecode.meteorframework.core.Domain.findType";

	}

	public static interface Type {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Type";

		// properties
		public static final String allDefaultedProperties = "meteor:com.googlecode.meteorframework.core.Type.allDefaultedProperties";
		public static final String allProperties = "meteor:com.googlecode.meteorframework.core.Type.allProperties";
		public static final String declaredDefaultedProperties = "meteor:com.googlecode.meteorframework.core.Type.declaredDefaultedProperties";
		public static final String declaredMethods = "meteor:com.googlecode.meteorframework.core.Type.declaredMethods";
		public static final String declaredProperties = "meteor:com.googlecode.meteorframework.core.Type.declaredProperties";
		public static final String extensionOf = "meteor:com.googlecode.meteorframework.core.Type.extensionOf";
		public static final String extensions = "meteor:com.googlecode.meteorframework.core.Type.extensions";
		public static final String fieldOrder = "meteor:com.googlecode.meteorframework.core.Type.fieldOrder";
		public static final String javaType = "meteor:com.googlecode.meteorframework.core.Type.javaType";
		public static final String namespace = "meteor:com.googlecode.meteorframework.core.Type.namespace";
		public static final String playedBy = "meteor:com.googlecode.meteorframework.core.Type.playedBy";
		public static final String rolesPlayed = "meteor:com.googlecode.meteorframework.core.Type.rolesPlayed";
		public static final String scalar = "meteor:com.googlecode.meteorframework.core.Type.scalar";
		public static final String singleton = "meteor:com.googlecode.meteorframework.core.Type.singleton";
		public static final String subTypes = "meteor:com.googlecode.meteorframework.core.Type.subTypes";
		public static final String superTypes = "meteor:com.googlecode.meteorframework.core.Type.superTypes";
		public static final String calar = "meteor:com.googlecode.meteorframework.core.Type.calar";
		public static final String ingleton = "meteor:com.googlecode.meteorframework.core.Type.ingleton";

		// methods
		public static final String addExtensionOf = "meteor:com.googlecode.meteorframework.core.Type.addExtensionOf";
		public static final String clearExtensionOf = "meteor:com.googlecode.meteorframework.core.Type.clearExtensionOf";
		public static final String isAssignableFrom = "meteor:com.googlecode.meteorframework.core.Type.isAssignableFrom";
		public static final String isSuperTypeOf = "meteor:com.googlecode.meteorframework.core.Type.isSuperTypeOf";
		public static final String removeExtensionOf = "meteor:com.googlecode.meteorframework.core.Type.removeExtensionOf";

	}

	public static interface Scope {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Scope";

		// properties
		public static final String nestedScopes = "meteor:com.googlecode.meteorframework.core.Scope.nestedScopes";

		// methods
		public static final String createScope = "meteor:com.googlecode.meteorframework.core.Scope.createScope";
		public static final String loadClass = "meteor:com.googlecode.meteorframework.core.Scope.loadClass";

	}

	public static interface Actor {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Actor";

		// methods
		public static final String exit = "meteor:com.googlecode.meteorframework.core.Actor.exit";
		public static final String send = "meteor:com.googlecode.meteorframework.core.Actor.send";

	}

	public static interface Provider {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Provider";

		// properties
		public static final String nestedProviders = "meteor:com.googlecode.meteorframework.core.Provider.nestedProviders";

		// methods
		public static final String createInstance = "meteor:com.googlecode.meteorframework.core.Provider.createInstance";
		public static final String findInstance = "meteor:com.googlecode.meteorframework.core.Provider.findInstance";
		public static final String getInstance = "meteor:com.googlecode.meteorframework.core.Provider.getInstance";
		public static final String injectMembers = "meteor:com.googlecode.meteorframework.core.Provider.injectMembers";
		public static final String putInstance = "meteor:com.googlecode.meteorframework.core.Provider.putInstance";

	}

	public static interface Method {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Method";

		// properties
		public static final String advisedBy = "meteor:com.googlecode.meteorframework.core.Method.advisedBy";
		public static final String advises = "meteor:com.googlecode.meteorframework.core.Method.advises";
		public static final String domain = "meteor:com.googlecode.meteorframework.core.Method.domain";
		public static final String range = "meteor:com.googlecode.meteorframework.core.Method.range";

		// methods
		public static final String invoke = "meteor:com.googlecode.meteorframework.core.Method.invoke";

	}

	public static interface Repository {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Repository";

		// properties
		public static final String allResources = "meteor:com.googlecode.meteorframework.core.Repository.allResources";
		public static final String nestedRepositories = "meteor:com.googlecode.meteorframework.core.Repository.nestedRepositories";

		// methods
		public static final String addResource = "meteor:com.googlecode.meteorframework.core.Repository.addResource";
		public static final String findResourceByURI = "meteor:com.googlecode.meteorframework.core.Repository.findResourceByURI";
		public static final String findResourcesByType = "meteor:com.googlecode.meteorframework.core.Repository.findResourcesByType";

	}

	public static interface Namespace {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Namespace";

		// properties
		public static final String dependencies = "meteor:com.googlecode.meteorframework.core.Namespace.dependencies";
		public static final String dependents = "meteor:com.googlecode.meteorframework.core.Namespace.dependents";
		public static final String types = "meteor:com.googlecode.meteorframework.core.Namespace.types";

		// methods
		public static final String compareTo = "meteor:com.googlecode.meteorframework.core.Namespace.compareTo";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Namespace$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.Namespace$Constructor.create";

		}

	}

	public static interface Resource {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Resource";

		// properties
		public static final String description = "meteor:com.googlecode.meteorframework.core.Resource.description";
		public static final String facets = "meteor:com.googlecode.meteorframework.core.Resource.facets";
		public static final String label = "meteor:com.googlecode.meteorframework.core.Resource.label";
		public static final String role = "meteor:com.googlecode.meteorframework.core.Resource.role";
		public static final String roles = "meteor:com.googlecode.meteorframework.core.Resource.roles";
		public static final String sameAs = "meteor:com.googlecode.meteorframework.core.Resource.sameAs";
		public static final String scope = "meteor:com.googlecode.meteorframework.core.Resource.scope";
		public static final String type = "meteor:com.googlecode.meteorframework.core.Resource.type";
		public static final String uRI = "meteor:com.googlecode.meteorframework.core.Resource.uRI";

		// methods
		public static final String addDecorator = "meteor:com.googlecode.meteorframework.core.Resource.addDecorator";
		public static final String addProperty = "meteor:com.googlecode.meteorframework.core.Resource.addProperty";
		public static final String castTo = "meteor:com.googlecode.meteorframework.core.Resource.castTo";
		public static final String clearProperty = "meteor:com.googlecode.meteorframework.core.Resource.clearProperty";
		public static final String equals = "meteor:com.googlecode.meteorframework.core.Resource.equals";
		public static final String getContainedProperties = "meteor:com.googlecode.meteorframework.core.Resource.getContainedProperties";
		public static final String getProperty = "meteor:com.googlecode.meteorframework.core.Resource.getProperty";
		public static final String getPropertyValues = "meteor:com.googlecode.meteorframework.core.Resource.getPropertyValues";
		public static final String hashCode = "meteor:com.googlecode.meteorframework.core.Resource.hashCode";
		public static final String isInstanceOf = "meteor:com.googlecode.meteorframework.core.Resource.isInstanceOf";
		public static final String postConstruct = "meteor:com.googlecode.meteorframework.core.Resource.postConstruct";
		public static final String removeDecorator = "meteor:com.googlecode.meteorframework.core.Resource.removeDecorator";
		public static final String removeProperty = "meteor:com.googlecode.meteorframework.core.Resource.removeProperty";
		public static final String setProperty = "meteor:com.googlecode.meteorframework.core.Resource.setProperty";
		public static final String toString = "meteor:com.googlecode.meteorframework.core.Resource.toString";

	}

	public static interface Property {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Property";

		// properties
		public static final String composite = "meteor:com.googlecode.meteorframework.core.Property.composite";
		public static final String defaultValue = "meteor:com.googlecode.meteorframework.core.Property.defaultValue";
		public static final String domain = "meteor:com.googlecode.meteorframework.core.Property.domain";
		public static final String equivalentOf = "meteor:com.googlecode.meteorframework.core.Property.equivalentOf";
		public static final String indexed = "meteor:com.googlecode.meteorframework.core.Property.indexed";
		public static final String indexedType = "meteor:com.googlecode.meteorframework.core.Property.indexedType";
		public static final String inverseOf = "meteor:com.googlecode.meteorframework.core.Property.inverseOf";
		public static final String many = "meteor:com.googlecode.meteorframework.core.Property.many";
		public static final String ordered = "meteor:com.googlecode.meteorframework.core.Property.ordered";
		public static final String range = "meteor:com.googlecode.meteorframework.core.Property.range";
		public static final String readOnly = "meteor:com.googlecode.meteorframework.core.Property.readOnly";
		public static final String reference = "meteor:com.googlecode.meteorframework.core.Property.reference";
		public static final String temporal = "meteor:com.googlecode.meteorframework.core.Property.temporal";
		public static final String unique = "meteor:com.googlecode.meteorframework.core.Property.unique";
		public static final String writeOnce = "meteor:com.googlecode.meteorframework.core.Property.writeOnce";
		public static final String writeOnly = "meteor:com.googlecode.meteorframework.core.Property.writeOnly";
		public static final String omposite = "meteor:com.googlecode.meteorframework.core.Property.omposite";
		public static final String ndexed = "meteor:com.googlecode.meteorframework.core.Property.ndexed";
		public static final String any = "meteor:com.googlecode.meteorframework.core.Property.any";
		public static final String rdered = "meteor:com.googlecode.meteorframework.core.Property.rdered";
		public static final String eadOnly = "meteor:com.googlecode.meteorframework.core.Property.eadOnly";
		public static final String eference = "meteor:com.googlecode.meteorframework.core.Property.eference";
		public static final String emporal = "meteor:com.googlecode.meteorframework.core.Property.emporal";
		public static final String nique = "meteor:com.googlecode.meteorframework.core.Property.nique";
		public static final String riteOnce = "meteor:com.googlecode.meteorframework.core.Property.riteOnce";
		public static final String riteOnly = "meteor:com.googlecode.meteorframework.core.Property.riteOnly";

	}

	public static interface Node {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.Node";

		// properties
		public static final String children = "meteor:com.googlecode.meteorframework.core.Node.children";
		public static final String parents = "meteor:com.googlecode.meteorframework.core.Node.parents";

	}

	public static interface ModelElement {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.ModelElement";

		// properties
		public static final String deploymentTypes = "meteor:com.googlecode.meteorframework.core.ModelElement.deploymentTypes";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
