package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.binding.Testing;

import junit.framework.TestCase;


/**
 * Base class for Meteor tests.
 * Sets up the test for dependency injection. 
 *  
 * @author Ted Stockwell
 */
public class BaseMeteorTest 
extends TestCase 
{
	private Scope _systemScope;
	private Scope _testScope;
	
	@Override protected void setUp() throws Exception
	{
		// create @Testing context and perform injection into this test case
		_systemScope= Meteor.getSystemScope();		
		Testing testing= _systemScope.getInstance(Testing.class);
		_testScope= _systemScope.createScope(testing);
		_testScope.injectMembers(this);
	}
}
