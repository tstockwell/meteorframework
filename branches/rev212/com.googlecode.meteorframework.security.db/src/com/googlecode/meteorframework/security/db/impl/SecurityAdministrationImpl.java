package com.googlecode.meteorframework.security.db.impl;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.security.Permission;
import com.googlecode.meteorframework.security.SecurityNS;
import com.googlecode.meteorframework.security.User;
import com.googlecode.meteorframework.security.UserGroup;
import com.googlecode.meteorframework.security.db.DatabaseSecurityAdministrationService;
import com.googlecode.meteorframework.security.db.DatabaseSecurityService;
import com.googlecode.meteorframework.security.db.DbNS;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.StorageTask;

@Decorator 
public abstract class SecurityAdministrationImpl implements DatabaseSecurityAdministrationService
{
	
	@Decorator public static class ConstructorImpl 
	implements DatabaseSecurityAdministrationService.Constructor 
	{
		@Inject Scope _scope;
		@Override public DatabaseSecurityAdministrationService create(DatabaseSecurityService service)
		{
			DatabaseSecurityAdministrationService service2= _scope.createInstance(DatabaseSecurityAdministrationService.class);
			((Resource)service2).setProperty(SecurityNS.SecurityAdministrationService.securityService, service);
			((Resource)service2).setProperty(DbNS.DatabaseSecurityAdministrationService.storageService, service.getStorageService());
			return service2;
		}
	}
	
	@Decorates private DatabaseSecurityAdministrationService _self;

	@Override
	public void addPermission(final Permission<?> permission)
	{
		_self.getStorageService().run(new StorageTask<Void>() {
    		@Override public Void run(StorageSession session)
    		{
    			session.persist(permission);
    			return null;
    		}
    	});
	}

	@Override
	public void addUser(final User user)
	{
    	_self.getStorageService().run(new StorageTask<Void>() {
    		@Override public Void run(StorageSession session)
    		{
    			String oldPassword= user.getPassword();
    			String shaPassword= DigestUtils.shaHex(oldPassword);
    			((Resource)user).setProperty(SecurityNS.User.password, shaPassword);
    			try {
    				session.persist(user);
    			}
    			finally {
        			((Resource)user).setProperty(SecurityNS.User.password, oldPassword);
    			}
    			return null;
    		}
    	});
	}

	@Override
	public void addUserGroup(final UserGroup group)
	{
		_self.getStorageService().run(new StorageTask<Void>() {
    		@Override public Void run(StorageSession session)
    		{
    			session.persist(group);
    			return null;
    		}
    	});
	}

	@Override
	public List<UserGroup> findGroups(final Selector<UserGroup> query)
	{
    	return _self.getStorageService().run(new StorageTask<List<UserGroup>>() {
    		@Override public List<UserGroup> run(StorageSession session)
    		{
    			return session.list(query);
    		}
    	});
	}

	@Override
	public List<Permission<?>> findPermissions(final Selector<Permission<?>> query)
	{
    	return _self.getStorageService().run(new StorageTask<List<Permission<?>>>() {
    		@Override public List<Permission<?>> run(StorageSession session)
    		{
    			return session.list(query);
    		}
    	});
	}

	@Override
	public List<User> findUsers(final Selector<User> query)
	{
    	return _self.getStorageService().run(new StorageTask<List<User>>() {
    		@Override public List<User> run(StorageSession session)
    		{
    			return session.list(query);
    		}
    	});
	}

	@Override
	public void flush()
	{
		_self.getStorageService().run(new StorageTask<Void>() {
    		@Override public Void run(StorageSession session)
    		{
    			session.flush();
    			return null;
    		}
    	});
	}
}
