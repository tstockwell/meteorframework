package com.googlecode.meteorframework.security.test;

import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.security.annotation.IsSecuredAction;
import com.googlecode.meteorframework.security.annotation.IsSecuredType;

@IsSecuredType
@ModelElement public interface SomeService
{
	@IsSecuredAction()
	public void securedMethod();
}
