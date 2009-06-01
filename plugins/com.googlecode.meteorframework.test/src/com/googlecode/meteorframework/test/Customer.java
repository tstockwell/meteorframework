package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.DefaultValue;
import com.googlecode.meteorframework.annotation.IsWriteOnce;
import com.googlecode.meteorframework.annotation.Model;

@Model public interface Customer extends Resource {
	
	@Model public interface Constructor extends Service {
		public Customer create(String name);
	}
	
	public String getName();
	public void setName(String value);
	
	public String someMethod();

	
	@DefaultValue(BootstrapTests.DESCRIPTION)
	public String getPropertyWithADefaultValue();
	public void setPropertyWithADefaultValue(String value);
	
	public String getPropertyWithNoSetMethod();
	
	@IsWriteOnce public void setWriteOnceProperty(String string);
	public String getWriteOnceProperty();
}
