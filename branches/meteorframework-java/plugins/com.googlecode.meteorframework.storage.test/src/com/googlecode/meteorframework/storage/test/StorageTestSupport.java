package com.googlecode.meteorframework.storage.test;

import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.security.AuthorizationContext;

@ModelElement public interface StorageTestSupport
{
	public AuthorizationContext getAuthorizationContext();
}
