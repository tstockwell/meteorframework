package com.googlecode.meteorframework.core.query;

import java.util.Set;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;


/**
 * Restricts a property to a set of values.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@ModelElement public interface InRestriction<T> extends Restriction {
	
	@ModelElement public interface Constructor  extends Service
	{
		public <T> InRestriction<T> create(Property<T> property, Set<T> value);
	}
	
	public Property<T> getProperty();
	
	public Set<T> getValues();

}
