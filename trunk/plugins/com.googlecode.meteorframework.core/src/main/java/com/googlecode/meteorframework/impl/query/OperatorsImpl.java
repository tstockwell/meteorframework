package com.googlecode.meteorframework.impl.query;

import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.query.Operator;
import com.googlecode.meteorframework.query.Operators;

/**
 * A service that defines basic operators
 * @author ted stockwell
 */
@Decorator public abstract class OperatorsImpl 
implements Operators, Resource 
{
	
	private Operator eq;
	private Operator lt;
	private Operator gt;
	private Operator like;
	private Operator ge;
	private Operator le;
	private Operator ne;
	
	private Operator or;
	private Operator and;
	private Operator not;
	private Operator isNull;
	
	@Inject Operator.Constructor opConstructor;
	
	@Override public void postConstruct() {
		eq= opConstructor.create("=");
		lt= opConstructor.create("<");
		gt= opConstructor.create(">");
		like= opConstructor.create("LIKE");
		ge= opConstructor.create(">=");
		le= opConstructor.create("<=");
		ne= opConstructor.create("<>");
		
		or= opConstructor.create("OR");
		and= opConstructor.create("AND");
		not= opConstructor.create("NOT");
		isNull= opConstructor.create("IS NULL");
	}
	
	@Override public Operator and() { return and; }
	@Override public Operator eq() { return eq; }
	@Override public Operator ge() { return ge; }
	@Override public Operator gt() { return gt; }
	@Override public Operator le() { return le; }
	@Override public Operator like() { return like; }
	@Override public Operator lt() { return lt; }
	@Override public Operator ne() { return ne; }
	@Override public Operator not() { return not; }
	@Override public Operator or() { return or; }

	@Override public Operator isNull() { return isNull; }

}
