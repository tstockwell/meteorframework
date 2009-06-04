package com.googlecode.meteorframework.test;

import java.math.BigDecimal;

import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;


@Decorator public abstract class CustomerStatementImpl implements CustomerStatement {
	
	@Decorator public static abstract class Constructor
	implements CustomerStatement.Constructor
	{
		@Inject private Scope _scope;
		
		@Override public CustomerStatement create(Customer customer) {
			if (customer == null)
				throw new RuntimeException("A customer must be supplied");
			CustomerStatement newStatement= _scope.createInstance(CustomerStatement.class);
			newStatement.setCustomer(customer);
			newStatement.setTitle("Customer Statement");
			newStatement.setOutstandingAmount(new BigDecimal("0.00"));
			return newStatement;
		}
	}
}

