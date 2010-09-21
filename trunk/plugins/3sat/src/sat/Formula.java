package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
	abstract public boolean evaluate(Map<String, Boolean> valuation);

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
	 * @return -1 if this formula is a substitution instance of the given formula.
	 * 	Otherwise return the position from the left in the given template formula where matching failed.
	 */
	public int isInstanceOf(Formula template) {
		Map<String, Formula> formulaBindings= new TreeMap<String, Formula>();
		return isInstanceOf(template, formulaBindings);
	}
	abstract protected int isInstanceOf(Formula template, Map<String, Formula> formulaBindings);
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Formula) == false)
			return false;
			
		return getFormulaText().equals(((Formula)obj).getFormulaText());
	}

}
