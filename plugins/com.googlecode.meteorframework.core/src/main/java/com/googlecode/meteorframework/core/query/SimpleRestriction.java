package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;

/**
 * Selects objects by the value of a property.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@Model public interface SimpleRestriction<T> extends Restriction {
	
	@Model public interface Constructor  extends Service
	{
		public <T> SimpleRestriction<T> create(Property<T> property, Operator operator, T value);
	}
	
	
	public Property<T> getProperty();
	
	public T getValue();
	
	public Operator getOperator();

}
