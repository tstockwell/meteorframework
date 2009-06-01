package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;

/**
 * This subclass always returns the same value from the getName method.
 * Used to test subclasses.
 * @author Ted Stockwell
 *
 */
@Model public interface CustomerSubclass extends Customer {
	
	public static final String NAME= "97809870987adsfasdfasdf";
	
	@Model public interface Constructor extends Service {
		public CustomerSubclass create(String name);
	}
}
