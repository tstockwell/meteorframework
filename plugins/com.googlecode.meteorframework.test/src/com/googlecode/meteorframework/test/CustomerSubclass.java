package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * This subclass always returns the same value from the getName method.
 * Used to test subclasses.
 * @author Ted Stockwell
 *
 */
@ModelElement public interface CustomerSubclass extends Customer {
	
	public static final String NAME= "97809870987adsfasdfasdf";
	
	@ModelElement public interface Constructor extends Service {
		public CustomerSubclass create(String name);
	}
}
