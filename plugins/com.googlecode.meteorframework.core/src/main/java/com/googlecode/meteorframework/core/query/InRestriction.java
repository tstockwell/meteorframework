package com.googlecode.meteorframework.core.query;

import java.util.Set;

import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * Restricts a property to a set of values.
 * @author ted stockwell
 *
 * @param <T>  The property's value type.
 */
@Model public interface InRestriction<T> extends Restriction {
	
	@Model public interface Constructor  extends Service
	{
		public <T> InRestriction<T> create(Property<T> property, Set<T> value);
	}
	
	public Property<T> getProperty();
	
	public Set<T> getValues();

}
