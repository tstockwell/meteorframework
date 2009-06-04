package com.googlecode.meteorframework.core.impl;

import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;


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
