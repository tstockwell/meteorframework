package com.googlecode.meteorframework.query;

import java.util.List;

import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;


/**
 * Creates a compound restriction.
 * @author ted stockwell
 */
@Model public interface CompoundRestriction extends Restriction {
	
	@Model public interface Constructor  extends Service
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
