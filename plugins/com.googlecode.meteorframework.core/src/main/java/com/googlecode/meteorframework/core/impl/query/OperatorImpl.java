package com.googlecode.meteorframework.core.impl.query;

import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.QueryNS;
import com.googlecode.meteorframework.core.query.Operator;


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
