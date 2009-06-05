package com.googlecode.meteorframework.test.extension;

import com.googlecode.meteorframework.core.annotation.ExtensionOf;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.test.Customer;
import com.googlecode.meteorframework.test.TestNS;



@ExtensionOf(TestNS.Customer.TYPE) 
@Model public interface CustomerExtension extends Customer {
	
	public String getTaxId();
	public void setTaxId(String value);

}
