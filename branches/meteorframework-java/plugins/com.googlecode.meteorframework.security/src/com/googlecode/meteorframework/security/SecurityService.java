package com.googlecode.meteorframework.security;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement public interface SecurityService 
extends Service
{
	public String getConnectionURL();
	
	 /**
     * Authenticates the user using the supplied <code>credentials</code>.
     */
    public AuthorizationContext login(Credentials credentials) throws SecurityException;
    
    public SecurityAdministrationService openManagementSession();
}
