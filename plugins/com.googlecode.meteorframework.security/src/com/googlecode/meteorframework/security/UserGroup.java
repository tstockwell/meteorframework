package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * UserGroups bind Users to a set of Permissions.
 * @author Ted Stockwell
 */
@ModelElement public interface UserGroup extends Resource
{
	@InverseOf(SecurityNS.User.userGroups)
	Set<User> getUsers();
	
	@InverseOf(SecurityNS.Permission.userGroup)
	Set<Permission<?>> getPermissions();
}
