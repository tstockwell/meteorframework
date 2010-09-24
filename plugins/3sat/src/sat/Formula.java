package sat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a propositional formula
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
abstract public class Formula {
	
	protected String _txt; // the textual normal form representation of this formula 
	
	Formula(String text) { _txt= text; }

	/**
	 * @param valuation  A collection that denotes boolean values for all variables 
	 * 
	 * @return true if the given valuation satisfies this formula, else false.
	 */
	abstract public boolean evaluate(Map<Variable, Boolean> valuation);

	public int length() {
		return _txt.length();
	}

	/**
	 * Returns an iterator that enumerates all the subformulas of this 
	 * formula, always enumerating the subformulas of antecedents before 
	 * the subformulas of consequences.
	 * This formula is the last formula returned.  
	 */
	abstract public Iterator<Formula> getLeftSidedDeepestFirstIterator(); 
	
	@Override
	public String toString() {
		return getFormulaText();
	}
	
	/**
	 * @return A textual representation of this formula in normal form.
	 * @see PropositionalSystem
	 */
	public String getFormulaText() {
		return _txt;
	}

	/**
	 * @return -1 if this formula subsumes the given formula, 
	 *  in other words, if the given formula is a substitution instance of this formula.
	 * 	Otherwise return the position from the left of this formula where matching failed.
	 */
	final public int subsumes(Formula formula) {
		return subsumes(formula, new HashMap<Variable, Formula>());
	}
	/**
	 * @see subsume(Formula)
	 * 
	 * Use this version if you also want to know what substitutions will 
	 * transform this formula into the given formula.
	 * The given map will be populated with appropriate substitutions.
	 */
	final public int subsumes(Formula formula, Map<Variable, Formula> variableSubstitions) {
		return findSubstutions(formula, variableSubstitions);
	}
	abstract protected int findSubstutions(Formula formula, Map<Variable, Formula> variableBindings);
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Formula) == false)
			return false;
			
		return getFormulaText().equals(((Formula)obj).getFormulaText());
	}

}
