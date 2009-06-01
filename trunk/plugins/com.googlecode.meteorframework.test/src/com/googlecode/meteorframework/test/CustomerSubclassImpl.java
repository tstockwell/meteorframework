package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;

/**
 * This subclass always returns the same value from the getName method.
 * Used to test subclasses.
 * @author Ted Stockwell
 *
 */
@Decorator public abstract class CustomerSubclassImpl 
implements CustomerSubclass, Resource 
{
	public static final String POST_CONSTRUCT_MARKER = ";CustomerSubclass.postConstruct called";
	
	@Decorates CustomerSubclass _self;
	
	@Decorator public static abstract class ConstructorImpl 
	implements CustomerSubclass.Constructor 
	{
		@Inject private Customer.Constructor _constructor;
		@Inject private Scope _scope;
		
		@Override  public CustomerSubclass create(String name) {
			CustomerSubclass customer= _constructor.create(name).castTo(CustomerSubclass.class);
			customer.setType(_scope.findType(CustomerSubclass.class));
			return customer;
		}
	}
	
	@Override public String getName()
	{
		return NAME;
	}

	public static boolean postConstructWasCalled(Customer customer)
	{
		String desc= customer.getDescription();
		if (desc == null)
			return false;
		int i= desc.indexOf(CustomerSubclassImpl.POST_CONSTRUCT_MARKER);
		if (i < 0)
			return false;
		int c= desc.indexOf(CustomerImpl.POST_CONSTRUCT_MARKER);
		if (i <= c)
			throw new RuntimeException("postConstruct in base class was called AFTER postConstruct in subclass");
		return true;
	}
}
