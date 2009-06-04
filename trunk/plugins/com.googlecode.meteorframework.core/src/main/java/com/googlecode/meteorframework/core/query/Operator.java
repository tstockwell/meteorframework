package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;


@Model public interface Operator extends Resource {
	
	@Model public interface Constructor extends Service {
		public Operator create(String operatorText);
	}
	
	public String getText();
	
	public String toString();
}
