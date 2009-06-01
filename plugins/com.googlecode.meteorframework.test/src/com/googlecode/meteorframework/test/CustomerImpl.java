package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.InvocationContext;
import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;

@Decorator public abstract class CustomerImpl implements Customer {
	
	public static final String POST_CONSTRUCT_MARKER = ";Customer.postConstruct called";

	static final String SET_INTERCEPTED_INDICATOR= "q-vm3t40n1=-68bn";
	
	@Decorates Customer _self;
	
	@Decorator public static class Constructor implements Customer.Constructor {
		@Inject private Scope _scope;
		
		@Override public Customer create(String name) {
			if (name == null)
				throw new RuntimeException("A name must be supplied");
			Customer customer= _scope.getInstance(Customer.class);
			customer.setName(name);
			return customer;
		}
	}
	
	
	@Override public void postConstruct()
	{
		String description= _self.getDescription();
		if (description == null)
			description= "";
		description+= POST_CONSTRUCT_MARKER;
		_self.setDescription(description);
	}
	
	
	@Override public String someMethod()
	{
		return "";
	}
	
	
	public void setPropertyWithNoSetMethod(String value) {
		String newValue= value+SET_INTERCEPTED_INDICATOR;
		InvocationContext ctx= Meteor.getInvocationContext();
		ctx.getArguments().set(0, newValue);
		ctx.proceed();
	}


	public static boolean postConstructWasCalled(Customer customer)
	{
		String desc= customer.getDescription();
		if (desc == null)
			return false;
		return 0 <= desc.indexOf(CustomerImpl.POST_CONSTRUCT_MARKER);
	}
}
