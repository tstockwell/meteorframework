package com.googlecode.meteorframework.core.query.jpa;

/**
 * Interface for operations over objects reached via paths
 */
public interface PathExpression extends Expression {
	/**
	 * Return a path expression corresponding to the referenced
	 * attribute.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association
	 * or element collection.
	 * The path expression on which this method is invoked must
	 * correspond to a class containing the referenced attribute.
	 * @param attributeName - name of the referenced attribute
	 * @return path expression
	 */
	PathExpression get(String attributeName);

	/**
	 * Return an expression that corresponds to the type
	 * of the entity.
	 * This method can only be invoked on a path expression
	 * corresponding to an entity.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association.
	 * @return expression denoting the entity's type
	 */
	Expression type();
	/**
	 * Return an expression that corresponds to the number
	 * of elements association or element collection corresponding
	 * to the path expression.
	 * This method can only be invoked on a path expression that
	 * corresponds to a multi-valued association or to an element
	 * collection.
	 * @return expression denoting the size
	 */
	Expression size();
	/**
	 * Add a restriction that the path expression must correspond
	 * to an association or element collection that is empty (has
	 * no elements).
	 * This method can only be invoked on a path expression that
	 * corresponds to a multi-valued association or to an element
	 * collection.
	 * @return predicate corresponding to the restriction
	 */
	Predicate isEmpty();
	/**
	 * Specify that the avg operation is to be applied.
	 * The path expression must correspond to an attribute of a
	 * numeric type.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association
	 * or element collection.
	 * @return the resulting aggregate
	 */
	Aggregate avg();
	/**
	 * Specify that the max operation is to be applied.
	 * The path expression must correspond to an attribute of
	 * an orderable type.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association
	 * or element collection.
	 * @return the resulting aggregate
	 */
	Aggregate max();

	/**
	 * Specify that the min operation is to be applied.
	 * The path expression must correspond to an attribute of
	 * an orderable type.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association
	 * or element collection.
	 * @return the resulting aggregate
	 */
	Aggregate min();
	/**
	 * Specify that the count operation is to be applied.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association
	 * or element collection.
	 * @return the resulting aggregate
	 */
	Aggregate count();
	/**
	 * Specify that the sum operation is to be applied.
	 * The path expression must correspond to an attribute of
	 * a numeric type.
	 * It is not permitted to invoke this method on a path
	 * expression that corresponds to a multi-valued association
	 * or element collection.
	 * @return the resulting aggregate
	 */
	Aggregate sum();
}