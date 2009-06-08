package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;

/**
 * Represents an action type that can be performed on some resource.
 * Examples: 
 * 	view an object, 
 * 		where 'view' is the action to be performed on some object that is viewable.
 *  save an object,
 *  delete an object,
 *  create an object,
 *  cancel an object,
 *  approve an object,
 *  deny a request,
 *  etc...
 *  
 * @author Ted Stockwell
 */
@Model public interface ActionType extends Resource 
{
	
	/**
	 * The elements on which this action may be performed
	 */
	@InverseOf(SecurityNS.SecurableType.actions)
	public Set<SecurableType<?>> getDomain();
}
