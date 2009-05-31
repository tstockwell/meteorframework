package com.googlecode.meteorframework.impl;

import com.googlecode.meteorframework.BindingType;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Decorator;


@SuppressWarnings("unchecked")
@Decorator public abstract class ScopeImpl 
implements Scope 
{
	@Decorates Scope _self;

	@Override public Class<?> loadClass(String driverClassName)
	throws ClassNotFoundException
	{
		return Activator.loadClass(driverClassName);
	}


	@Override public Scope createScope(BindingType... scopeAnnotations)
	{
		Resource resource= _self.getInstance(Resource.class, scopeAnnotations);
		resource.setType(Scope.class);
		
		Scope scope= resource.castTo(Scope.class);
		scope.getNestedScopes().add(_self);
		
		ObjectImpl impl= ObjectImpl.getObjectImpl(resource);
		impl._scope= impl;
		
		return scope;
	}

}
