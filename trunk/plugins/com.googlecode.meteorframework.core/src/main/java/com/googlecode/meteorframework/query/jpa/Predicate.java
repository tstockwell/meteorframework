package com.googlecode.meteorframework.query.jpa;

/**
 * Interface used to define compound predicates.
 */
public interface Predicate {
	/**
	 * Creates an AND of the predicate with the argument.
	 * @param predicate - A simple or compound predicate
	 * @return the predicate that is the AND of the original
	 * simple or compound predicate and the argument.
	 */
	Predicate and(Predicate predicate);
	/**
	 * Creates an OR of the predicate with the argument.
	 * @param predicate - A simple or compound predicate
	 * @return the predicate that is the OR of the original
	 * simple or compound predicate and the argument.
	 */
	Predicate or(Predicate predicate);
	/**
	 * Creates a negation of the predicate with the argument.
	 * @return the predicate that is the negation of the
	 * original simple or compound predicate.
	 */
	Predicate not();
}