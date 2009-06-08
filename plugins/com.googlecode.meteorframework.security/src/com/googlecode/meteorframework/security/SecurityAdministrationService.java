package com.googlecode.meteorframework.security;

import java.util.List;

import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.query.Selector;

@Model public interface SecurityAdministrationService 
{
	public SecurityService getSecurityService();
    
    public List<User> findUsers(Selector<User> query);
    public void addUser(User user);
    
    public List<UserGroup> findGroups(Selector<UserGroup> query);
    public void addUserGroup(UserGroup user);
    
    public List<Permission<?>> findPermissions(Selector<Permission<?>> query);
    public void addPermission(Permission<?> permission);

    /**
     * Save all changes to persistent objects to the underlying system.
     */
    public void flush();
    
}
