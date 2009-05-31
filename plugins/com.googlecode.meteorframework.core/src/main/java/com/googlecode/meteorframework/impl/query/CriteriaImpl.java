package com.googlecode.meteorframework.impl.query;

import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.Type;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.QueryNS;
import com.googlecode.meteorframework.query.Restriction;
import com.googlecode.meteorframework.query.Selector;

/**
 * Describes a query used to select a set of objects from a repository of objects.
 * 
 * @author ted stockwell
 *
 * @param <T> The type of objects that the Selector select.
 * 	The range property will return the type of objects that the Selector select.
 */
@SuppressWarnings("unchecked")
@Decorator public abstract class CriteriaImpl<T> implements Selector<T> {
	
	
	@Decorator static public abstract class Constructor implements Selector.Constructor {
		
		@Inject private Scope _scope;
		
		@Override public <T> Selector<T> create(Class<T> rangeClass, Restriction restriction) {
			Selector<T> query= _scope.createInstance(Selector.class);
			Type range= _scope.findType(rangeClass);
			if (range == null)
				throw new MeteorException("No Type found for java class:"+rangeClass);
			((Resource)query).setProperty(QueryNS.Selector.range, range);
			query.setRestriction(restriction);
			return query;
		}
	}
	
}
