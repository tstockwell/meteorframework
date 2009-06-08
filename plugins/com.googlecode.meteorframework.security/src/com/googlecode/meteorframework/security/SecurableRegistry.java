package com.googlecode.meteorframework.security;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.Model;

/**
 * Used by security implements to discover all the SecurableTypes in the 
 * system.
 * 
 * A system extension may extend a system with new categories of securable 
 * objects by decorating SecurableRegistry and adding new categories to the 
 * set of returned categories.
 * 
 * @author ted stockwell
 */
@Model public interface SecurableRegistry
{
	public Set<SecurableCategory> getAllSecurableCategories();
}
