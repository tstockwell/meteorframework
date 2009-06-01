package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Bind;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.binding.Testing;

@Model public interface SomeConfiguration extends Service
{
	public static final String BASE_VALUE="value";  
	public static final String TEST_VALUE="TESTING value";
	
	String getSomeProperty();
	String doSomething();
	
	
	
	
	@Decorator public static class SomeConfigurationImpl implements SomeConfiguration {
		@Override public String getSomeProperty() {
			return BASE_VALUE;
		}
		@Override public String doSomething() {
			return BASE_VALUE;
		}
	}
	
	@Bind(Testing.class)
	@Decorator public static class TestSomeConfigurationImpl implements SomeConfiguration {
		@Override public String getSomeProperty() {
			return TEST_VALUE;  
		}
		
		@Override public String doSomething() {
			return TEST_VALUE;
		}
	}
}
