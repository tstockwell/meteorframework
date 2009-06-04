package com.googlecode.meteorframework.common;

import java.util.Set;

import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * Used to arrange objects into a hierarchy
 *
 * @param <T> The types of objects arranged by thos category.
 * 
 * @author ted stockwell
 */
@Model public interface Category<T>
{
	/**
	 * The type of objects arranged by this category.
	 */
	public Type<T> getRange();
	
	
	@InverseOf(CommonNS.Category.parent)
	public Set<Category<T>> getChildren();
	
	@InverseOf(CommonNS.Category.children)
	public Category<T> getParent();
	
}
