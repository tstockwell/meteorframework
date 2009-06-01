package com.googlecode.meteorframework.test;


public abstract class CustomerDecorator implements Customer {
	
	public static final String NAME="Decorated Csutomer Name";
	
	@Override public String getName()
	{
		return NAME;
	}
	
	@Override public String someMethod()
	{
		return NAME;
	}
}
