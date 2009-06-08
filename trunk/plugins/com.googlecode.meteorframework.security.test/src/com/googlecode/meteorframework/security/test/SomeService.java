package com.googlecode.meteorframework.security.test;

import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.security.annotation.IsSecuredAction;
import com.googlecode.meteorframework.security.annotation.IsSecuredType;

@IsSecuredType
@Model public interface SomeService
{
	@IsSecuredAction()
	public void securedMethod();
}
