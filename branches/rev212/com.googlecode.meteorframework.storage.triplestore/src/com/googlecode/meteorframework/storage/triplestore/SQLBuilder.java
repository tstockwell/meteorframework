package com.googlecode.meteorframework.storage.triplestore;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.query.Restriction;
import com.googlecode.meteorframework.core.query.Selector;
import com.truemesh.squiggle.Criteria;
import com.truemesh.squiggle.SelectQuery;

/**
 * Generates SQL select statements from Meteor Selector objects.
 * Uses the Squiggle SQL builder API to provide an object=oriented interface to SQL select statements.
 * 
 * @Service SQLBuilder.Constructor builderConstructor;
 * SelectQuery query= builderConstructor.create(criteria);
 * 
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface SQLBuilder
{
	
	@ModelElement public interface Constructor extends Service {
		public SelectQuery create(Selector<?> criteria);
	}
	
	public SelectQuery createSelectQuery(Selector<?> criteria); 
	public Criteria createCriteria(SelectQuery select, Restriction restriction);
	
}
