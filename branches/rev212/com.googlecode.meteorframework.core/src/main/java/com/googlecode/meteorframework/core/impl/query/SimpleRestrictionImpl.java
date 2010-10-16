package com.googlecode.meteorframework.core.impl.query;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.QueryNS;
import com.googlecode.meteorframework.core.query.Operator;
import com.googlecode.meteorframework.core.query.SimpleRestriction;

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
