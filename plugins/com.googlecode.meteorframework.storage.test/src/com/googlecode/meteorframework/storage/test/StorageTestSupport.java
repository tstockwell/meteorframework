package com.googlecode.meteorframework.storage.test;

import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.security.AuthorizationContext;

@Model public interface StorageTestSupport
{
	public AuthorizationContext getAuthorizationContext();
}
