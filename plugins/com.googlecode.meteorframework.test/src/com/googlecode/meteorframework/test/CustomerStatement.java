package com.googlecode.meteorframework.test;

import java.math.BigDecimal;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;


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

