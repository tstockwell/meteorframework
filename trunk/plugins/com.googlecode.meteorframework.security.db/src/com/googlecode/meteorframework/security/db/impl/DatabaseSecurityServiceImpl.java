package com.googlecode.meteorframework.security.db.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.Restrictions;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.security.AuthorizationContext;
import com.googlecode.meteorframework.security.Credentials;
import com.googlecode.meteorframework.security.Permission;
import com.googlecode.meteorframework.security.SecurityAdministrationService;
import com.googlecode.meteorframework.security.SecurityNS;
import com.googlecode.meteorframework.security.User;
import com.googlecode.meteorframework.security.UserGroup;
import com.googlecode.meteorframework.security.db.DatabaseSecurityAdministrationService;
import com.googlecode.meteorframework.security.db.DatabaseSecurityService;
import com.googlecode.meteorframework.security.db.DbNS;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.StorageTask;


/**
 * A security service that uses the Meteor Storage API to 
 * store configuration info.
 * 
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
@Decorator public abstract class DatabaseSecurityServiceImpl 
implements DatabaseSecurityService
{
	@Decorator public static abstract class FactoryImpl 
	implements DatabaseSecurityService.Constructor
	{
		@Inject Scope _scope;
		
		@Override public DatabaseSecurityService create(String serviceURL, StorageService storageService)
		{
			DatabaseSecurityService service= _scope.createInstance(DatabaseSecurityService.class);
			((Resource)service).setProperty(SecurityNS.SecurityService.connectionURL, serviceURL);
			((Resource)service).setProperty(DbNS.DatabaseSecurityService.storageService, storageService);
			return service;
		}	
	}
	
	@Inject private Restrictions _restrictions;
	@Inject private Selector.Constructor _selectors;
	@Inject private DatabaseSecurityAdministrationService.Constructor _adminServices;
	@Inject private AuthorizationContext.Constructor _authorizations;
	
	@Decorates DatabaseSecurityService _self;
	
	/**
     * Authenticates the user using the supplied <code>credentials</code>.
     */
	@Override public AuthorizationContext login(final Credentials credentials) 
    throws SecurityException
    {
    	return _self.getStorageService().run(new StorageTask<AuthorizationContext>() {
    		@Override public AuthorizationContext run(StorageSession session)
    		{
    	    	String loginId= credentials.getLoginID();
    	    	if (loginId == null || loginId.length() <= 0)
    	    		throw new SecurityException("Missing user name");
    	    	Selector<User> query= _selectors.create(User.class, 
    	    			_restrictions.propertyEq(SecurityNS.User.loginID, loginId));
    			List<User> users= session.list(query);
    			if (users.isEmpty())
    				throw new SecurityException("No such User:"+loginId);
    			User user= users.get(0);
    			
    			String password= credentials.getPassword();
    	    	if (password == null || password.length() <= 0)
    	    		throw new SecurityException("Missing password");
    			password= DigestUtils.shaHex(password);
    			if (!user.getPassword().equals(password))
    				throw new SecurityException("Password is not valid");
    			
    			Set<UserGroup> userGroups= user.getUserGroups();
    	    	Selector<Permission> permissionQuery= _selectors.create(Permission.class, 
    	    			_restrictions.propertyIn(SecurityNS.Permission.userGroup, userGroups));
    			List<Permission> permissions= session.list(permissionQuery);
    			return _authorizations.create(user, new HashSet(permissions));
    		}
    	});
    }
    
    
    @Override public SecurityAdministrationService openManagementSession()
    {
    	return _adminServices.create(_self);
    }
}
