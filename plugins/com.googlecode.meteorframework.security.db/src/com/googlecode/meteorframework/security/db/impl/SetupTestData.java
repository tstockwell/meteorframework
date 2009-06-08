package com.googlecode.meteorframework.security.db.impl;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.annotation.After;
import com.googlecode.meteorframework.core.annotation.Bind;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.binding.Testing;
import com.googlecode.meteorframework.security.AuthorizationContext;
import com.googlecode.meteorframework.security.SecurityAdministrationService;
import com.googlecode.meteorframework.security.SecurityService;
import com.googlecode.meteorframework.security.SecurityServiceFactory;
import com.googlecode.meteorframework.security.User;

@Decorator public abstract class SetupTestData 
implements SecurityServiceFactory
{
	@Inject User.Constructor _users;
	@Inject AuthorizationContext.Constructor _authorizations;
	
	@Override @After @Bind(Testing.class)
	public SecurityService create(String connectionURL)
	{
		SecurityService service= (SecurityService)Meteor.getResult();
		SecurityAdministrationService administrationService= service.openManagementSession();
		
		User user= _users.create("test", "test");
		administrationService.addUser(user);
		
		return service;
	}
}
