package com.googlecode.meteorframework.storage.appengine.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.CompoundRestriction;
import com.googlecode.meteorframework.core.query.InRestriction;
import com.googlecode.meteorframework.core.query.Operator;
import com.googlecode.meteorframework.core.query.Operators;
import com.googlecode.meteorframework.core.query.Restriction;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.core.query.SimpleRestriction;
import com.googlecode.meteorframework.core.query.URIRestriction;
import com.googlecode.meteorframework.storage.UnsupportedStorageException;
import com.googlecode.meteorframework.storage.appengine.QueryBuilder;

@Decorator
public abstract class QueryBuilderImpl
implements QueryBuilder
{
	@Inject DatastoreService _datastoreService;
	@Inject Operators _ops;
	@Decorates QueryBuilder _self;
	
	
	
	public Query convert(Selector<?> selector, Class<Query> type) {
		Query query= new Query(selector.getRange().getURI());
		
		Restriction restriction= selector.getRestriction();
		if (restriction != null)
			_self.addFilter(query, restriction);
		
		return query;
	}
	
	public void addFilter(Query query, CompoundRestriction restriction) {
		if (!_ops.and().equals(restriction.getOperator()))
			throw new UnsupportedStorageException("'OR' resitrictions are not supported");
		for (Restriction restriction2 : restriction.getRestrictions())
		{
			_self.addFilter(query, restriction2);
		}
	}
	
	public void addFilter(Query query, URIRestriction restriction)
	{
		query.addFilter(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, restriction.getValue());
	}

	public void addFilter(Query query, SimpleRestriction<?> restriction)
	{
		Query.FilterOperator filterOperator= null;
		Operator operator= restriction.getOperator();
		if (_ops.eq().equals(operator))
		{
			filterOperator= Query.FilterOperator.EQUAL;
		}
		else if (_ops.gt().equals(operator))
		{
			filterOperator= Query.FilterOperator.GREATER_THAN;
		}
		else if (_ops.ge().equals(operator))
		{
			filterOperator= Query.FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		else if (_ops.lt().equals(operator))
		{
			filterOperator= Query.FilterOperator.LESS_THAN;
		}
		else if (_ops.le().equals(operator))
		{
			filterOperator= Query.FilterOperator.LESS_THAN_OR_EQUAL;
		}
		else 
		{
			throw new UnsupportedStorageException("Operator is not supported by the Google storage engine:"+operator);
		}
		
		query.addFilter(restriction.getProperty().getURI(), filterOperator, restriction.getValue());
	}


	public void addFilter(Query query, InRestriction<?> restriction)
	{
		throw new UnsupportedStorageException("InRestriction is not currently supported by the Google storage engine");
	}
	

}
