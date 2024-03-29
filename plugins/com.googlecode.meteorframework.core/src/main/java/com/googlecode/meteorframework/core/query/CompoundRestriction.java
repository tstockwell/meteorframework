package com.googlecode.meteorframework.core.query;

import java.util.List;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;


/**
 * Creates a compound restriction.
 * @author ted stockwell
 */
@ModelElement public interface CompoundRestriction extends Restriction {
	
	@ModelElement public interface Constructor  extends Service
	{
		/**
		 * @param operation  Must be And, OR, or Not operator
		 * @param restriction2 May be null if created with Not operator
		 */
		public CompoundRestriction create(Operator operation, Restriction restriction, Restriction restriction2);
	}
	
	public List<Restriction> getRestrictions();

	public Operator getOperator();

}
