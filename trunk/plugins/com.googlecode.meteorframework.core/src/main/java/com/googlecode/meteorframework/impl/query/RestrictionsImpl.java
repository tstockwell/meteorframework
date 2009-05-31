package com.googlecode.meteorframework.impl.query;

import java.util.Set;

import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Property;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.Type;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.annotation.Lookup;
import com.googlecode.meteorframework.impl.ObjectImpl;
import com.googlecode.meteorframework.impl.RepositoryImpl;
import com.googlecode.meteorframework.query.CompoundRestriction;
import com.googlecode.meteorframework.query.InRestriction;
import com.googlecode.meteorframework.query.Operators;
import com.googlecode.meteorframework.query.Restriction;
import com.googlecode.meteorframework.query.Restrictions;
import com.googlecode.meteorframework.query.SimpleRestriction;
import com.googlecode.meteorframework.query.URIRestriction;


@SuppressWarnings("unchecked")
@Decorator public class RestrictionsImpl implements Restrictions {
	
	private @Lookup(MeteorNS.Resource.type) Property<Type> typeProperty;
	private @Inject URIRestriction.Constructor uriCriterionConstructor;
	private @Inject SimpleRestriction.Constructor simpleCriterionConstructor;
	private @Inject InRestriction.Constructor _inRestrictions;
	private @Inject CompoundRestriction.Constructor compoundConstructor;
	private @Inject Operators _operators;
	private @Inject Scope _repository;
	
	@Decorates Restrictions _self;
	
	@Override public Restriction uriEq(Resource reference) {
		return _self.uriEq(ObjectImpl.getObjectImpl(reference).internalGetURI());
	}
	@Override public Restriction uriEq(String uri) {
		return uriCriterionConstructor.create(uri);
	}
	@Override public <T> Restriction propertyEq(Property<T> property, T value) {
		return simpleCriterionConstructor.create(property, _operators.eq(), value);
	}
	@Override public Restriction typeEq(Type value) {
		return simpleCriterionConstructor.create(typeProperty, _operators.eq(), value);
	}
	
	@Override public Restriction and(Restriction restriction1, Restriction restriction2)
	{
		return compoundConstructor.create(_operators.and(), restriction1, restriction2);
	}
	
	@Override public Restriction or(Restriction restriction1, Restriction restriction2)
	{
		return compoundConstructor.create(_operators.or(), restriction1, restriction2);
	}
	
	@Override public Restriction not(Restriction restriction)
	{
		return compoundConstructor.create(_operators.not(), restriction, null);
	}
	
	@Override public Restriction propertyEq(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyEq(property, value);
	}
	
	@Override public Restriction propertyIn(String propertyURI, Set value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyIn(property, value);
	}
	
	@Override public <T> Restriction propertyIn(Property<T> property, Set<T> value)
	{
		return _inRestrictions.create(property, value);
	}
	@Override
	public <T> Restriction propertyGE(Property<T> property, T value)
	{
		return simpleCriterionConstructor.create(property, _operators.ge(), value);
	}
	@Override
	public Restriction propertyGE(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyGE(property, value);
	}
	@Override
	public <T> Restriction propertyGT(Property<T> property, T value)
	{
		return simpleCriterionConstructor.create(property, _operators.gt(), value);
	}
	@Override
	public Restriction propertyGT(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyGT(property, value);
	}
	@Override
	public <T> Restriction propertyIsNull(Property<T> property)
	{
		return simpleCriterionConstructor.create(property, _operators.isNull(), null);
	}
	@Override
	public Restriction propertyIsNull(String propertyURI)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyIsNull(property);
	}
	@Override
	public <T> Restriction propertyLE(Property<T> property, T value)
	{
		return simpleCriterionConstructor.create(property, _operators.le(), value);
	}
	@Override
	public Restriction propertyLE(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyLE(property, value);
	}
	@Override
	public <T> Restriction propertyLT(Property<T> property, T value)
	{
		return simpleCriterionConstructor.create(property, _operators.lt(), value);
	}
	@Override
	public Restriction propertyLT(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyLT(property, value);
	}
	@Override
	public <T> Restriction propertyLike(Property<T> property, T value)
	{
		return simpleCriterionConstructor.create(property, _operators.like(), value);
	}
	@Override
	public Restriction propertyLike(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyLike(property, value);
	}
	@Override
	public <T> Restriction propertyNE(Property<T> property, T value)
	{
		return simpleCriterionConstructor.create(property, _operators.ne(), value);
	}
	@Override
	public Restriction propertyNE(String propertyURI, Object value)
	{
		Property<Object> property= RepositoryImpl.findResourceByURI(_repository, propertyURI, Property.class);
		if (property == null)
			throw new MeteorException("Property Not Found:"+propertyURI);
		return _self.propertyNE(property, value);
	}

}
