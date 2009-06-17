package com.googlecode.meteorframework.security;

import com.googlecode.meteorframework.common.Category;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Used to arrange SecurableTypes into a hierarchy.
 * 
 * @author ted stockwell
 */
@ModelElement public interface SecurableCategory extends Category<SecurableType<?>>
{

}
