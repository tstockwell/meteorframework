package com.googlecode.meteorframework.impl.query;

import java.util.Collection;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.CompoundRestriction;
import com.googlecode.meteorframework.query.Operator;
import com.googlecode.meteorframework.query.QueryNS;
import com.googlecode.meteorframework.query.Restriction;


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
