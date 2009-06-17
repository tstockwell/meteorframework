package com.googlecode.meteorframework.core;

import com.googlecode.meteorframework.core.annotation.Model;


@Model public interface ModelElement
{
	/**
	 * Denotes bindings that must always apply to the associated model element.
	 * For example, if the associated model element is a Type method then this 
	 * property indicates that the denoted bindings should always be added 
	 * to the calling context when a call is made to the associated method.
	 * If the associated model element is a Decorator method then this 
	 * property indicates that the associated method is only invoked when 
	 * the denoted bindings match the current calling context.
	 * 
	 * Bindings are used to implement interceptors and to customize generic 
	 * methods depending of the current binding context.
	 *  
	 * For example, implementing cross-cutting transaction management 
	 * consists of the following steps:
	 * 
	 * ...define a BindingType named Transactional.
	 * 
	 * ...use the @Bind annotation to add the Transactional binding type to  
	 * method declarations (specifically, the Bind annotation adds the Transactional 
	 * binding type to the bindings property of the associated Method metadata).
	 * 
	 * ...use the @Bind annotation to add the Transactional binding type to 
	 * the decorator that implements the transaction handling.
	 *  
	 */
	BindingContext getBindings();
}
