package com.googlecode.meteorframework.query.jpa;

/**
 * Type of the result of an aggregate operation
 */
public interface Aggregate extends Expression {
	/**
	 * Specify that duplicates are to be removed before
	 * the aggregate operation is invoked.
	 */
	Expression distinct();
}
