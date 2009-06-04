package com.googlecode.meteorframework.core.impl;

import java.util.List;

import com.googlecode.meteorframework.core.Domain;
import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Namespace;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorator;



@SuppressWarnings("unchecked")
@Decorator abstract public class DomainImpl 
implements Domain 
{

	@Override public Namespace findNamespace(Package javaPackage) 
	{
		InvocationContext ctx= Meteor.getInvocationContext();
		ObjectImpl impl= ObjectImpl.getObjectImpl(ctx.getReceiver());
		Scope scope= impl._scope.internalCast(Scope.class);
		String uri= Meteor.PROTOCOL+javaPackage.getName();
		Namespace namespace= findNamespace(scope, uri);
		if (namespace == null) {
			namespace= scope.getInstance(Namespace.Constructor.class).create(javaPackage);
			RepositoryImpl.addResource(scope, namespace);
		}
		return namespace;
	}

	private static Namespace findNamespace(Scope scope, String uri) 
	{
		Namespace namespace= RepositoryImpl.findResourceByURI(scope, uri, Namespace.class);
		if (namespace == null) 
			return namespace;
		
		List<Scope> nestedScopes= scope.getNestedScopes();
		if (nestedScopes != null) {
			for (Scope nestedScope : nestedScopes) {
				namespace= findNamespace(nestedScope, uri);
				if (namespace != null) 
					return namespace;
			}
		}
		
		return namespace;
	}


	@Override public <T> Type<T> findType(Class<T> javaType) {
		InvocationContext ctx= Meteor.getInvocationContext();
		Scope scope= ctx.getReceiver().getScope();
		return findType(scope, javaType);
	}
	
	private static Type<?> findType(Scope scope, String uri)
	{
		Type type= RepositoryImpl.findResourceByURI(scope, uri, Type.class);
		if (type != null) 
			return type;
		
		List<Scope> nestedScopes= scope.getNestedScopes();
		if (nestedScopes != null) {
			for (Scope nestedScope : nestedScopes) {
				type= findType(nestedScope, uri);
				if (type != null) 
					return type;
			}
		}
		
		return type;
	}


	public static <T> Type<T> findType(Scope scope, Class<T> javaType)
	{
		if (javaType.isPrimitive()) {
			javaType= BaseSpecializer._objectTypesByPrimitiveTypes.get(javaType);
		}
		
		String uri= Meteor.getURIForClass(javaType);
		Type type= findType(scope, uri);
		if (type != null) 
			return type;
		
		type= TypeAdvice.createType(scope, javaType);
		RepositoryImpl.addResource(scope, type);
		return type;
	}

}
