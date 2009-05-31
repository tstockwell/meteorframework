package com.googlecode.meteorframework.query;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;


@Model public interface Operator extends Resource {
	
	@Model public interface Constructor extends Service {
		public Operator create(String operatorText);
	}
	
	public String getText();
	
	public String toString();
}
