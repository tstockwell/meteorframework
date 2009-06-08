package com.googlecode.meteorframework.security.impl;

import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.security.SecurityConfiguration;
import com.googlecode.meteorframework.security.SecurityService;
import com.googlecode.meteorframework.security.SecurityServiceFactory;

@Decorator 
public abstract class SecurityServiceFactoryImpl 
implements SecurityServiceFactory
{
	@Decorates SecurityServiceFactory _self;
	@Inject SecurityConfiguration _configuration;
	
	@Override public SecurityService create() {
		return _self.create(_configuration.getDefaultConnectionURL());
	}

	
	/**
	 * If this method is actually invoked it means that there are no 
	 * installed security implementation that know how to connect to 
	 * the given service. 
	 */
	@Override public SecurityService create(String connectionURL)
	{
		throw new SecurityException("Don't know how to connect to security server at "+connectionURL);
	}
}
