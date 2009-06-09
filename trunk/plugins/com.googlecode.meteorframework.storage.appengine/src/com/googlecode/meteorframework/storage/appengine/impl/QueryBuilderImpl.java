package com.googlecode.meteorframework.storage.appengine.impl;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.Restriction;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.core.query.jpa.QueryBuilder;
import com.googlecode.meteorframework.core.utils.ConversionService;

@Decorator
public abstract class QueryBuilderImpl
implements QueryBuilder
{
	@Inject DatastoreService _datastoreService;
	
	
	
	public Query convert(Selector<?> selector, Class<Query> type) {
		Query query= new Query(selector.getRange().getURI());
		
		Restriction restriction= selector.getRestriction();
		if (restriction != null)
			query.select.addCriteria(_self.createCriteria(select, restriction));
		return select;
		
		return null;
	}

}
