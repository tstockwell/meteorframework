package com.googlecode.meteorframework.test.extension;

import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.ExtensionOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.test.Customer;



@ExtensionOf(Customer.class) 
@ModelElement public interface CustomerExtension extends Customer {
	
	public static final String DEFAULT_VALUE= "the default valuel;kj"; 
	
	
	public String getTaxId();
	public void setTaxId(String value);
	
	
	@DefaultValue(DEFAULT_VALUE)
	String getSomePropertyWithADefaultValue();

	
}
