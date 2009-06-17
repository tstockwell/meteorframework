package com.googlecode.meteorframework.test.extension;

import com.googlecode.meteorframework.core.annotation.ExtensionOf;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.test.Customer;
import com.googlecode.meteorframework.test.TestNS;



@ExtensionOf(TestNS.Customer.TYPE) 
@ModelElement public interface CustomerExtension extends Customer {
	
	public String getTaxId();
	public void setTaxId(String value);

}
