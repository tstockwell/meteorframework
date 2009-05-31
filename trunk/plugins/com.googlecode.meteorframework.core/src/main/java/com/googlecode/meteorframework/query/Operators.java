package com.googlecode.meteorframework.query;

import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;

/**
 * A service that defines basic operators
 * @author ted stockwell
 */
@Model public interface Operators extends Service {
	
	public Operator eq();
	public Operator lt();
	public Operator gt();
	public Operator like();
	public Operator ge();
	public Operator le();
	public Operator ne();
	
	public Operator isNull();
	
	public Operator or();
	public Operator and();
	public Operator not();
	
}
