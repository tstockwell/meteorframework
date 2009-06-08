package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.common.Person;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;

@Model public interface User extends Person
{
	@Model public interface Constructor extends Service {
		User create(String loginID, String password);
	}
	
	
	/**
	 * Get the user groups to which this User belongs.
	 */
	@InverseOf(SecurityNS.UserGroup.users)
	public Set<UserGroup> getUserGroups();

	
	public String getLoginID();
	public String getPassword();
}
