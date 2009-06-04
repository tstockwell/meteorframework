package com.googlecode.meteorframework.test;

import java.math.BigDecimal;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;


@Model public interface CustomerStatement extends Resource {
	
	@Model public interface Constructor extends Service {
		public CustomerStatement create(Customer customer);
	}
	
	public Customer getCustomer();
	public void setCustomer(Customer customer);

	public String getTitle();
	public void setTitle(String title);
	
	public BigDecimal getOutstandingAmount();
	public void setOutstandingAmount(BigDecimal value);


}

