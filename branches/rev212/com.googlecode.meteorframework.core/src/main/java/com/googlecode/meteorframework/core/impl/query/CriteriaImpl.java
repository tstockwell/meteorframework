package com.googlecode.meteorframework.core.impl.query;

import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.QueryNS;
import com.googlecode.meteorframework.core.query.Restriction;
import com.googlecode.meteorframework.core.query.Selector;

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
