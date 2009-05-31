package com.googlecode.meteorframework.query.jpa;

/**
 * Constructor interface for query definition objects
 */
public interface QueryBuilder {
	/**
	 * Create an uninitialized query definition object.
	 * @return query definition instance
	 */
	QueryDefinition createQueryDefinition();
	/**
	 * Create a query definition object with the given root.
	 * The root must be an entity class.
	 * @param cls - an entity class
	 * @return root domain object
	 */
	DomainObject createQueryDefinition(Class root);
	/**
	 * Create a query definition object whose root is derived from
	 * a domain object of the containing query.
	 * Provides support for correlated subqueries. Joins against the
	 * resulting domain object do not affect the query domain of the
	 * containing query.
	 * The path expression must correspond to an entity class.
	 * The path expression must not be a domain object of the
	 * containing query.
	 * @param path - path expression corresponding to the domain
	 * object used to derive the subquery root.
	 * @return the subquery DomainObject
	 */
	DomainObject createSubqueryDefinition(PathExpression path);
}