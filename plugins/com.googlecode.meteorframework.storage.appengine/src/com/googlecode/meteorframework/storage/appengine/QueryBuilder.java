package com.googlecode.meteorframework.storage.appengine;

import com.google.appengine.api.datastore.Query;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.query.Restriction;
import com.googlecode.meteorframework.core.query.Selector;

/**
 * Generates App Engine Query objects from Meteor Selector objects.
 * 
 * Sample usage:
 * @Inject QueryBuilder queryBuilder;
 * Query query= queryBuilder.create(selector);
 * 
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface QueryBuilder
{
	public Query createQuery(Selector<?> criteria); 
	public void addFilter(Query query, Restriction restriction);
	
}
