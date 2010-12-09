/*
 * Copyright 2005 Day Management AG, Switzerland. All rights reserved.
 */
package com.googlecode.meteorframework.security;

import java.io.Serializable;

import com.googlecode.meteorframework.core.annotation.FieldOrder;
import com.googlecode.meteorframework.core.annotation.Label;
import com.googlecode.meteorframework.core.annotation.ModelElement;

@FieldOrder({SecurityNS.Credentials.loginID})
@ModelElement public interface Credentials extends Serializable {
	
	@Label("User") 
	String 	getLoginID();
	void setLoginID(String loginID);

	String getPassword();
	void setPassword(String password);

}