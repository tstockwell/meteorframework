package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Selects objects by URI.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@ModelElement public interface URIRestriction extends Restriction {
	
	@ModelElement public interface Constructor extends Service{
		public URIRestriction create(Operator operator, String uri);
		
		public URIRestriction create(String uri);
	}
	
	public Operator getOperator();
	
	public String getValue();
}
