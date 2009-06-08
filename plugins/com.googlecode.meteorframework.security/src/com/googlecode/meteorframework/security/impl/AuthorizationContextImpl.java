package com.googlecode.meteorframework.security.impl;

import java.util.Set;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.security.AuthorizationContext;
import com.googlecode.meteorframework.security.Permission;
import com.googlecode.meteorframework.security.SecurityNS;
import com.googlecode.meteorframework.security.User;

@Decorator public abstract class AuthorizationContextImpl 
implements AuthorizationContext
{
	@Decorator public static class ConstructorImpl 
	implements AuthorizationContext.Constructor
	{
		@Inject Scope _scope;
		@Override public AuthorizationContext create(User user, Set<Permission<?>> permissions)
		{
			AuthorizationContext context= _scope.createInstance(AuthorizationContext.class);
			((Resource)context).setProperty(SecurityNS.AuthorizationContext.user, user);
			((Resource)context).setProperty(SecurityNS.AuthorizationContext.permissions, permissions);
			return context;
		}
	}
}
