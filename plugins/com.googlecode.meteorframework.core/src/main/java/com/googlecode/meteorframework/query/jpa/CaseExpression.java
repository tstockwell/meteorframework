package com.googlecode.meteorframework.query.jpa;

import java.util.Calendar;
import java.util.Date;

/**
 * Interface for the construction of case expressions
 */
public interface CaseExpression {
	/**
	 * Add a when predicate clause to a general case expression.
	 * The when predicate must be followed by the corresponding
	 * then case expression that specifies the result of the
	 * specific case.
	 * Clauses are evaluated in the order added.
	 * @param pred - corresponds to the evaluation condition
	 * for the specific case
	 * @return CaseExpression corresponding to the case
	 * with the added when clause
	 */
	CaseExpression when(Predicate pred);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(Expression when);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(Number when);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
Selector API Interfaces Java Persistence 2.0, Public Review Draft Selector API
JSR-317 Public Review Draft 185 10/31/08
Sun Microsystems, Inc.
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(String when);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(Date when);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(Calendar when);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(Class when);
	/**
	 * Add a when clause to a simple case expression.
	 * The when case expression must be followed by the
	 * corresponding then case expression that specifies the
	 * result of the specific case.
	 * Clauses are evaluated in the order added
	 * @param when - corresponds to the value against which
	 * the case operand of the simple case is tested
	 * @return CaseExpression corresponding to the case
	 * with the added clause
	 */
	CaseExpression when(Enum<?> when);

	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(Expression then);
	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(Number then);
	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(String then);
	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(Date then);
	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(Calendar then);

	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(Class then);
	/**
	 * Add a then clause to a general or simple case expression.
	 * The then clause specifies the result corresponding to
	 * the immediately preceding when.
	 * Clauses are evaluated in the order added.
	 * @param then - corresponds to the result of the case
	 * expression if the when is satisfied
	 * @return CaseExpression corresponding to the case
	 * with the added then clause
	 */
	CaseExpression then(Enum<?> then);
	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(Expression arg);
	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(String arg);
	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(Number arg);

	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(Date arg);
	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(Calendar arg);
	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(Class arg);
	/**
	 * Add else to a case expression.
	 * A case expression must have an else clause.
	 * @param arg - corresponds to the result of the case
	 * expression if the when condition is
	 * not satisfied
	 * @return Expression corresponding to the case expression
	 * with the added clause
	 */
	Expression elseCase(Enum<?> arg);
}