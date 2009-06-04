package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.IsWriteOnce;
import com.googlecode.meteorframework.core.annotation.Model;

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
