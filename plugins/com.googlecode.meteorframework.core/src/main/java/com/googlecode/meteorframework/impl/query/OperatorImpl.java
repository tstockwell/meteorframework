package com.googlecode.meteorframework.impl.query;

import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.Operator;
import com.googlecode.meteorframework.query.QueryNS;


@Decorator public abstract class OperatorImpl implements Operator {
	
	@Decorator public static class Constructor implements Operator.Constructor 
	{
		@Inject private Scope _scope;
		
		@Override public Operator create(String operatorText) {
			Operator operator= _scope.createInstance(Operator.class);
			operator.setProperty(QueryNS.Operator.text, operatorText);
			return operator;
		}
	}

	@Override public String toString() {
		return getText();
	}
}
