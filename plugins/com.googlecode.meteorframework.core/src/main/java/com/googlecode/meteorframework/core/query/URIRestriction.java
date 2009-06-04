package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;

/**
 * Selects objects by URI.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@Model public interface URIRestriction extends Restriction {
	
	@Model public interface Constructor extends Service{
		public URIRestriction create(Operator operator, String uri);
		
		public URIRestriction create(String uri);
	}
	
	public Operator getOperator();
	
	public String getValue();
}
