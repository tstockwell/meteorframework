package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * A type that represents a role. 
 *  
 * @author ted stockwell
 */
@ModelElement
public interface Role<T> extends Type<T> {
	
	/**
	 * The actor types that can play this role. 
	 */
	@InverseOf(CoreNS.Type.rolesPlayed)
	Set<Type<?>> getPlayedBy();
}
