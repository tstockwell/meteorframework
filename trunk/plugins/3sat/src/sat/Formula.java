package sat;

import java.util.Iterator;
import java.util.Map;

/**
 * Represents a propositional formula
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
abstract public class Formula implements Comparable<Formula> {

	protected String _txt; // the textual normal form representation of this formula 

	Formula(String text) { _txt= text; }
	
	@Override
	public int compareTo(Formula formula) {
		return _txt.compareTo(formula._txt);
	}

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Formula) == false)
			return false;
			
		return getFormulaText().equals(((Formula)obj).getFormulaText());
	}

}
