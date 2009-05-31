package com.googlecode.meteorframework.impl.query;

import java.util.Set;

import com.googlecode.meteorframework.Property;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.InRestriction;
import com.googlecode.meteorframework.query.QueryNS;


/**
 * Selects objects by the value of a property.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@Decorator public abstract class InRestrictionImpl<T> implements InRestriction<T> {
	
	@Decorator public static class Constructor implements InRestriction.Constructor 
	{
		@Inject private Scope _scope;
		
		@SuppressWarnings("unchecked")
		@Override public <T> InRestriction<T> create(Property<T> property, Set<T> values) {
			InRestriction<T> criterion= _scope.createInstance(InRestriction.class);
			((Resource)criterion).setProperty(QueryNS.InRestriction.property, property);
			((Resource)criterion).setProperty(QueryNS.InRestriction.values, values);
			return criterion;
		}
	}
	

}
