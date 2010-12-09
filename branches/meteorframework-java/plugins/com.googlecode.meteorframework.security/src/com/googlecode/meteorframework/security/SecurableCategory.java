package com.googlecode.meteorframework.security;

import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.common.Category;

/**
 * Used to arrange SecurableTypes into a hierarchy.
 * 
 * @author ted stockwell
 */
@ModelElement public interface SecurableCategory extends Category<SecurableType<?>>
{

}
