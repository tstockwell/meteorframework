package com.googlecode.meteorframework.core.impl.query;

import java.util.Collection;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.QueryNS;
import com.googlecode.meteorframework.core.query.CompoundRestriction;
import com.googlecode.meteorframework.core.query.Operator;
import com.googlecode.meteorframework.core.query.Restriction;


/**
 * @author ted stockwell
 */
public abstract class CompoundRestrictionImpl implements CompoundRestriction {
	
	@Decorator public static class Constructor implements CompoundRestriction.Constructor {
		
		@Inject private Scope _scope;
		
		@Override public CompoundRestriction create(Operator operation, Restriction restriction, Restriction restriction2)
		{
			CompoundRestriction restriction3= _scope.createInstance(CompoundRestriction.class);
			Collection<Restriction> collection= restriction3.getRestrictions();
			collection.add(restriction);
			collection.add(restriction2);
			((Resource)collection).setProperty(QueryNS.CompoundRestriction.operator, operation);
			return restriction3;
		}
	}
	

}
