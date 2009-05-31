package com.googlecode.meteorframework.impl.query;

import com.googlecode.meteorframework.Property;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.Operator;
import com.googlecode.meteorframework.query.QueryNS;
import com.googlecode.meteorframework.query.SimpleRestriction;

/**
 * Selects objects by the value of a property.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@Decorator public abstract class SimpleRestrictionImpl<T> implements SimpleRestriction<T> {
	
	@Decorator public static class Constructor implements SimpleRestriction.Constructor {
		
		@Inject private Scope _scope;
		
		@SuppressWarnings("unchecked")
		@Override public <T> SimpleRestriction<T> create(Property<T> property, Operator operator, T value) {
			SimpleRestriction<T> criterion= _scope.createInstance(SimpleRestriction.class);
			((Resource)criterion).setProperty(QueryNS.SimpleRestriction.property, property);
			((Resource)criterion).setProperty(QueryNS.SimpleRestriction.value, value);
			((Resource)criterion).setProperty(QueryNS.SimpleRestriction.operator, operator);
			return criterion;
		}
	}
	

}
