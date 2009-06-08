package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.annotation.ExtensionOf;
import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;

/**
 * Represents a type of object that may be secured.
 * 
 * @param the java types that this securable type represents.
 * 
 * @author ted stockwell
 */
@ExtensionOf(MeteorNS.Resource.TYPE)
@Model public interface SecurableType<T>
{
	/**
	 * Returns the actions that may be performed on this type 
	 */
	@InverseOf(SecurityNS.ActionType.domain)
	public Set<ActionType> getActions();
	
}
