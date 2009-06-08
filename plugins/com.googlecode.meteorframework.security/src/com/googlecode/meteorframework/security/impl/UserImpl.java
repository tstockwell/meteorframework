package com.googlecode.meteorframework.security.impl;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.security.SecurityNS;
import com.googlecode.meteorframework.security.User;

public class UserImpl
{
	@Decorator public static class ConstructorImpl 
	implements User.Constructor {
		@Inject private Scope _scope;
		@Override
		public User create(String loginID, String password)
		{
			User user= _scope.createInstance(User.class);
			((Resource)user).setProperty(SecurityNS.User.loginID, loginID);
			((Resource)user).setProperty(SecurityNS.User.password, password);
			return user;
		}
	}
}
