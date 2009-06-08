package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.IsMethod;
import com.googlecode.meteorframework.core.annotation.IsReadOnly;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.query.Selector;

/**
 * An <code>AuthorizationContext</code> object provides information about the 
 * permissions granted to a User.
 */
@Model public interface AuthorizationContext {
	
	@Model public interface Constructor extends Service {
		AuthorizationContext create(User user, Set<Permission<?>> permissions);
	}
	
	/**
	 * Returns the User associated with this context.
	 */
	public User getUser();
	
	/**
	 * Returns all permissions granted to the User.
	 */
	@IsReadOnly
	public Set<Permission<?>> getPermissions();


    /**
     * Determines whether the User associated with this context has permission 
     * to execute the specified action on the given resource.
     * @return true if permission is granted.
     */
    public <T> boolean checkPermission(T resource, ActionType securableAction);
    

    /**
     * Returns Selectors that denotes exactly for what set 
     * of objects the user has then given permission.
     * The User has permissions on an object that belongs to any of the sets 
     * of objects defined by the set of selectors.
     */
    @IsMethod
    public <T> Set<Selector<T>> getPermissionConstraints(T resource, ActionType securableAction);

}