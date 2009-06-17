package com.googlecode.meteorframework.security.test;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Provider;
import com.googlecode.meteorframework.core.annotation.Binding;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.binding.Testing;
import com.googlecode.meteorframework.security.Credentials;

@Decorator public abstract class ProviderImpl 
implements Provider
{
	@Decorates Provider _self;
	
	@Binding(Testing.class)
	public Credentials getInstance(Class<Credentials> class1)
	{
		Credentials credentials= (Credentials)Meteor.proceed();
		credentials.setLoginID("test");
		credentials.setPassword("test");
		return credentials;
	}
	
}
