package com.googlecode.meteorframework.impl.query;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.Operator;
import com.googlecode.meteorframework.query.Operators;
import com.googlecode.meteorframework.query.QueryNS;
import com.googlecode.meteorframework.query.URIRestriction;

/**
 * Selects objects by URI.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@Decorator public abstract class URIRestrictionImpl implements URIRestriction {
	
	@Decorator public static class Constructor 
	implements URIRestriction.Constructor 
	{
		@Inject private Scope _scope;
		@Inject private Operators _operators;
		@Decorates private URIRestriction.Constructor _self;
		
		@Override public URIRestriction create(Operator operator, String uri) {
			URIRestriction criterion= _scope.createInstance(URIRestriction.class);
			((Resource)criterion).setProperty(QueryNS.URIRestriction.value, uri);
			((Resource)criterion).setProperty(QueryNS.URIRestriction.operator, operator);
			return criterion;
		}
		
		@Override public URIRestriction create(String uri) {
			return _self.create(_operators.eq(), uri);
		}
	}
}
