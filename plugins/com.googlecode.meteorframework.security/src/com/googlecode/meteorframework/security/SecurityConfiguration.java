package com.googlecode.meteorframework.security;

import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.Model;

@Model public interface SecurityConfiguration
{
	/**
	 * The URL of the default security service.
	 * Uses database backed security by default.
	 */
	@DefaultValue("meteor:security:db")
	public String getDefaultConnectionURL();
}
