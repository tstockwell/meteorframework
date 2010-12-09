package com.googlecode.meteorframework.security.impl;

import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.security.AuthorizationContext;
import com.googlecode.meteorframework.security.Credentials;
import com.googlecode.meteorframework.security.SecurityService;
import com.googlecode.meteorframework.security.SecurityServiceFactory;
import com.googlecode.meteorframework.security.User;

@Decorator public abstract class ProviderImpl implements Provider
{
	@Decorates Provider _self;
	
	public SecurityService getInstance(Class<SecurityService> class1)
	{
		return _self.getInstance(SecurityServiceFactory.class).create();
	}

	public User getInstance(Class<User> class1)
	{
		SecurityService service= _self.getInstance(SecurityService.class);
		Credentials credentials= _self.getInstance(Credentials.class);
		return service.login(credentials).getUser();
	}

	public AuthorizationContext getInstance(Class<AuthorizationContext> class1)
	{
		SecurityService service= _self.getInstance(SecurityService.class);
		Credentials credentials= _self.getInstance(Credentials.class);
		return service.login(credentials);
	}
}
