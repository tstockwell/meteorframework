package com.googlecode.meteorframework.security;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.query.Restriction;

/**
 * Provides information about the circumstances in which a 
 * Use will be granted permission to execute an action.
 * 
 * Meteor supports data-driven permission configuration.
 * Data-driven configuration enables constraints to be 
 * applied to a permission.
 * 
 * For instance, it should be possible to grant permission to view a particular 
 * report only on a given day of the week.  For such a permission type the
 * associated Permission would denote what day of the week the 
 * permission should be granted.
 * 
 * A more realistic example is to grant a Salesperson permission to only view 
 * Orders that were created by the Salesperson.  
 * 
 * Permissions are typically configured by administrators and 
 * are saved in a database.
 * 
 * @param <T>  The Java type to which this permission belongs.
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface Permission<T>
{
	/**
	 * action to which this configuration applies
	 */
	ActionType getAction();
	
	/**
	 * type of objects to which this permission applies
	 */
	SecurableType<T> getType();
	
	/**
	 * User group to which this configuration applies
	 */
	@InverseOf(SecurityNS.UserGroup.permissions)
	UserGroup getUserGroup();
	
	/**
	 * Optional Restriction that defines constraints on the permission. 
	 */
	Restriction getRestriction();
}
