package com.googlecode.meteorframework.core.utils;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;


/**
 * Provides hints about how objects should be ordered.
 * @author Ted Stockwell
 *
 */
@ModelElement public interface OrderHintProvider
{
	/**
	 * The set of objects that should come after this object.
	 */
	@InverseOf(UtilsNS.OrderHintProvider.before)
	Set<Object> getAfter();
	
	/**
	 * The set of objects that should come before this object.
	 */
	@InverseOf(UtilsNS.OrderHintProvider.after)
	Set<Object> getBefore();
}
