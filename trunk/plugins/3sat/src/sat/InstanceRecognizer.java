package sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A utility for recognizing substitution instances of a set of formulas.
 * 
 * A InstanceRecognizer instance is populated with a set of formulas.
 * The InstanceRecognizer can then be used to determine if a given formula is a 
 * substitution instance of any of the formulas in the InstanceRecognizer.
 * 
 * InstanceRecognizer implements an efficient method for recognizing 
 * substitution instances.
 * A InstanceRecognizer builds an internal trie structure for representing all 
 * the given formulas that enables it to avoid many comparisons, thus making it 
 * much more efficient than testing all formulas.      
 *  
 *  
 * @author Ted Stockwell
 *
 */
public class InstanceRecognizer extends PropositionalSystem {
	
	
	HashSet<Formula> _formulas= new HashSet<Formula>();
	
	
	@Override
	public Formula createFormula(String path) {
		Formula formula= super.createFormula(path);
		_formulas.add(formula);
		return formula;
	}
	@Override
	public Formula createNegation(Formula right) {
		Formula formula= super.createNegation(right);
		_formulas.add(formula);
		return formula;
	}
	@Override
	public Formula createImplication(Formula consequent, Formula antecedent) {
		Formula formula= super.createImplication(consequent, antecedent);
		_formulas.add(formula);
		return formula;
	}
	@Override
	public Formula createVariable(int variable) {
		Formula formula= super.createVariable(variable);
		_formulas.add(formula);
		return formula;
	}
	@Override
	public Formula createInstance(Formula templateFormula, HashMap<Formula, Formula> substitutions) {
		Formula formula= super.createInstance(templateFormula, substitutions);
		_formulas.add(formula);
		return formula;
	}
	
	
	
	/**
	 * Find all formulas for which the given formula is a substitution instance.
	 */
	public Iterator<Match> findMatches(Formula formula) {
		Map<String, String> substitions= new HashMap<String, String>();
		return _root.findMatches(formula.getFormulaText(), substitions);
	}
	/**
	 * Find the first substitution instance.
	 * @returns null if there is no match
	 */
	public Match findFirstMatch(Formula formula) {
		Map<String, String> substitions= new HashMap<String, String>();
		Iterator<Match> i= _root.findMatches(formula.getFormulaText(), substitions);
		if (i.hasNext())
			return i.next();
		return null;
	}
	/**
	 * Find the first substitution instance.
	 * @returns null if there is no match
	 */
	public List<Match> findAllMatches(Formula formula) {
		Map<String, String> substitions= new HashMap<String, String>();
		Iterator<Match> i= _root.findMatches(formula.getFormulaText(), substitions);
		ArrayList<Match> all= new ArrayList<Match>();
		while (i.hasNext())
			all.add(i.next());
		return all;
	}
	public void addFormula(Formula formula) {
		createFormula(formula.getFormulaText());
	}
	
}
