package com.googlecode.meteorframework.security.test;

import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.security.AuthorizationContext;
import com.googlecode.meteorframework.security.Credentials;
import com.googlecode.meteorframework.security.SecurityService;
import com.googlecode.meteorframework.security.User;
import com.googlecode.meteorframework.test.BaseMeteorTest;

public class SecurityTests extends BaseMeteorTest
{
	@Inject private Credentials _credentials;
	@Inject private SecurityService _securityService;
	
	public void testSimpleLogin() {
		AuthorizationContext authorizationContext= _securityService.login(_credentials);
		assertNotNull(authorizationContext);
		User user= authorizationContext.getUser();
		assertNotNull(user);
		assertEquals(_credentials.getLoginID(), user.getLoginID());
	}
	
}
