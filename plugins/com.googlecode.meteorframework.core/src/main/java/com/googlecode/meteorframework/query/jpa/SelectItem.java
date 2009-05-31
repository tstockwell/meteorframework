package com.googlecode.meteorframework.query.jpa;

/**
 * SelectItem instances are used in specifying the query's
 * select list.
 *
 * The methods of this interface are used to define arguments
 * that can be passed to the orderBy method for use in ordering
 * selected items of the query result.
 */
public interface SelectItem extends OrderByItem {
	/**
	 * Return an OrderByItem referencing the SelectItem and
	 * specifying ascending ordering.
	 * The SelectItem must correspond to an orderable value.
	 * @return order-by item
	 */
	OrderByItem asc();
	/**
	 * Return an OrderByItem referencing the SelectItem and
	 * specifying descending ordering.
	 * The SelectItem must correspond to an orderable value.
	 * @return order-by item
	 */
	OrderByItem desc();
}