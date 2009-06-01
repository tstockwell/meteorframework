package com.googlecode.meteorframework.test.extension;

import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.test.Customer;
import com.googlecode.meteorframework.test.CustomerStatement;


/**
 * Customize the CustomerStatement constructor.
 * Create custom titles on customer statements 
 */
@Decorator public abstract class CustomerStatementAdvice 
implements CustomerStatement 
{
	
	@Decorator public static abstract class Constructor 
	implements CustomerStatement.Constructor 
	{
		/**
		 * Customizes the CustomerStatement constructor by setting the 
		 * statement title to an unflattering custom title.
		 */
		@Override public CustomerStatement create(Customer customer) {		
			CustomerStatement statement= (CustomerStatement)Meteor.proceed();
			statement.setTitle(getCustomTitle(customer));
			return statement;
		}
	}
	
	public static String getCustomTitle(Customer customer) {
		return "Customer Statement for "+customer.getName()+" (who, as you can see, *never* pays on time)";
	}
}
