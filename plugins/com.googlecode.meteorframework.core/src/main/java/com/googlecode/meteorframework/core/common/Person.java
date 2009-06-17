package com.googlecode.meteorframework.core.common;

import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement public interface Person
{
	String getFirstName();
	void setFirstName(String firstName);
	
	String getLastName();
	void setLastName(String lastName);
}
