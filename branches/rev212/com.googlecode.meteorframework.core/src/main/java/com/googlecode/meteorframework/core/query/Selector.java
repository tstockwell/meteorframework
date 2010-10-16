package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Describes criteria used to select a set of objects from a repository of objects.
 * 
 * @author ted stockwell
 *
 * @param <T> The type of objects that the Selector select.
 * 	The range property will return the type of objects that the Selector select.
 */
@ModelElement public interface Selector<T> {
	
	@ModelElement public interface Constructor extends Service {
		public <T> Selector<T> create(Class<T> rangeClass, Restriction restriction);
	}
	
//	public Path getPath();
//	
//	public  Selector<?> join(String path);
//	/**
//	 * A more strict way to create a join
//	 */
//	public <E> Selector<E> join(String path, Class<E> type);
	
	/**
	 * The Type selected by this query
	 */
	public Type<T> getRange();
	
	public Restriction getRestriction();
	public void setRestriction(Restriction restriction);
}
