package net.sf.meteor.storage.triplestore.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.meteor.storage.triplestore.SQLBuilder;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.CompoundRestriction;
import com.googlecode.meteorframework.core.query.InRestriction;
import com.googlecode.meteorframework.core.query.Operator;
import com.googlecode.meteorframework.core.query.Operators;
import com.googlecode.meteorframework.core.query.Restriction;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.core.query.SimpleRestriction;
import com.googlecode.meteorframework.core.query.URIRestriction;
import com.googlecode.meteorframework.core.utils.ConversionService;
import com.googlecode.meteorframework.storage.StorageException;
import com.truemesh.squiggle.Criteria;
import com.truemesh.squiggle.Literal;
import com.truemesh.squiggle.LiteralValueSet;
import com.truemesh.squiggle.SelectQuery;
import com.truemesh.squiggle.Table;
import com.truemesh.squiggle.ValueSet;
import com.truemesh.squiggle.WildCardColumn;
import com.truemesh.squiggle.criteria.AND;
import com.truemesh.squiggle.criteria.InCriteria;
import com.truemesh.squiggle.criteria.IsNullCriteria;
import com.truemesh.squiggle.criteria.MatchCriteria;
import com.truemesh.squiggle.criteria.NOT;
import com.truemesh.squiggle.criteria.OR;

@Decorator public abstract class SQLBuilderImpl implements SQLBuilder
{
	private static final String	VALUE	= "VALUE";
	private static final String	PREDICATE	= "PREDICATE";
	public static final String	SUBJECT	= "SUBJECT";
	public static final String	STATEMENTS	= "STATEMENTS";
	
	@Decorator public static class ContructorImpl implements SQLBuilder.Constructor {
		@Inject Scope _scope;
		@Override public SelectQuery create(Selector<?> selector)
		{
			SQLBuilder builder= _scope.createInstance(SQLBuilder.class);
			return builder.createSelectQuery(selector);
		}
		
	}
	
	@Inject private Operators _operators;
	@Inject private ConversionService _conversionService;
	@Decorates private SQLBuilder _self;
	
	Table _records= new Table(STATEMENTS, "RECORDS");

	@Override public SelectQuery createSelectQuery(Selector<?> selector)
	{
		SelectQuery select= new SelectQuery();
		select.addToSelection(new WildCardColumn(_records));

		Table range= new Table(STATEMENTS, "RANGE");
		select.addJoin(_records, SUBJECT, range, SUBJECT);
		select.addCriteria(new MatchCriteria(range, PREDICATE, MatchCriteria.EQUALS, MeteorNS.Resource.type));
		select.addCriteria(new MatchCriteria(range, VALUE, MatchCriteria.EQUALS, selector.getRange().getURI()));
		
		Restriction restriction= selector.getRestriction();
		if (restriction != null)
			select.addCriteria(_self.createCriteria(select, restriction));
		return select;
	}
	
	public Criteria createCriteria(SelectQuery select, CompoundRestriction compoundRestriction)
	{
		Operator operator= compoundRestriction.getOperator();
		List<Restriction> restrictions= compoundRestriction.getRestrictions();
		if (operator.equals(_operators.and())) {
			return new AND(
					_self.createCriteria(select, restrictions.get(0)), 
					_self.createCriteria(select, restrictions.get(1)));
 		}
		else if (operator.equals(_operators.or())) {
			return new OR(
					_self.createCriteria(select, restrictions.get(0)), 
					_self.createCriteria(select, restrictions.get(1)));
		}
		else if (operator.equals(_operators.not())) {
			return new NOT(_self.createCriteria(select, restrictions.get(0)));
			
		}
		return (Criteria)Meteor.proceed();
	}
	
	protected Table addJoinForRestriction(SelectQuery select, Restriction restriction, String propertyURI)
	{
		String tableName= "CLAUSE"+select.listTables().size();
		Table clause= new Table(STATEMENTS, tableName);
		select.addJoin(_records, SUBJECT, clause, SUBJECT);
		if (propertyURI == null) { 
			select.addJoin(_records, PREDICATE, clause, PREDICATE);
		}
		else
			select.addCriteria(new MatchCriteria(clause, PREDICATE, MatchCriteria.EQUALS, propertyURI));
		return clause;
	}
	
	public Criteria createCriteria(SelectQuery select, URIRestriction restriction)
	{
		Table table= addJoinForRestriction(select, restriction, null);
		return new MatchCriteria(table, SUBJECT, restriction.getOperator().getText(), restriction.getValue());
	}

	public Criteria createCriteria(SelectQuery select, SimpleRestriction<?> restriction)
	{
		Table table= addJoinForRestriction(select, restriction, restriction.getProperty().getURI());
		Operator operator= restriction.getOperator();
		
		if (_operators.isNull().equals(operator)) 
			return new IsNullCriteria(table.getColumn(VALUE));

		Object value= restriction.getValue();
		Literal literal= null;
		try {
			literal= _conversionService.convert(value, Literal.class);
		}
		catch (MeteorException x) {
			throw new StorageException("Cannot convert a value to a SQL literal:"+value, x);
		}
		return new MatchCriteria(table.getColumn(VALUE), operator.getText(), literal);
	}


	public Criteria createCriteria(SelectQuery select, InRestriction<?> restriction)
	{
		Table table= addJoinForRestriction(select, restriction, restriction.getProperty().getURI());
		Set<?> values= restriction.getValues();
		ArrayList<Literal> literals= new ArrayList<Literal>();
		for (Object value : values) {
			Literal literal= null;
			try {
				literal= _conversionService.convert(value, Literal.class);
				literals.add(literal);
			}
			catch (MeteorException x) {
				throw new StorageException("Cannot convert a value to a SQL literal:"+value, x);
			}
		}
		ValueSet valueSet= new LiteralValueSet(literals);
		return new InCriteria(table.getColumn(VALUE), valueSet);
	}
	
}
