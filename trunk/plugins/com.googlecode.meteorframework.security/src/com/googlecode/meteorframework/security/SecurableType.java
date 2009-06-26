package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.ExtensionOf;
import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Represents a type of object that may be secured.
 * 
 * @param the java types that this securable type represents.
 * 
 * @author ted stockwell
 */
@ExtensionOf(Resource.class)
@ModelElement public interface SecurableType<T>
{
	/**
	 * Returns the actions that may be performed on this type 
	 */
	@InverseOf(SecurityNS.ActionType.domain)
	public Set<ActionType> getActions();
	
}
