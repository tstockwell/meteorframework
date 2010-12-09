package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;


@ModelElement public interface Operator extends Resource {
	
	@ModelElement public interface Constructor extends Service {
		public Operator create(String operatorText);
	}
	
	public String getText();
	
	public String toString();
}
