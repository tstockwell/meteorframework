package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Binding;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.binding.Testing;

@ModelElement public interface SomeConfiguration extends Service
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
	
	@Binding(Testing.class)
	@Decorator public static class TestSomeConfigurationImpl implements SomeConfiguration {
		@Override public String getSomeProperty() {
			return TEST_VALUE;  
		}
		
		@Override public String doSomething() {
			return TEST_VALUE;
		}
	}
}
