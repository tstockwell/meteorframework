package com.googlecode.meteorframework.core.impl.query;

import java.util.Set;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.QueryNS;
import com.googlecode.meteorframework.core.query.InRestriction;


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
