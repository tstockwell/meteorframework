package sat;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a propositional formula.
 * 
 * This system represents propositional formulas using only...
 * 	...constants TRUE and FALSE.
 *  ...variables.
 *  ...a negation operator 
 *  ...an implication operator (aka the if-then operator, most often written as ->).
 *  
 * This system supports a textual form for representing formulas.
 * The normal form uses Polish notation and...
 * 	...the symbols 'T' and 'F' for TRUE and FALSE.
 *  ...the symbol '-' for the negation operator, followed by a formula. 
 *  ...the symbol '*' for the implication operator, followed by the consequent 
 *  	and then the antecedent. 
 *  ...the symbol '^' followed by a number for representing variables.
 *  
 *  Polish notation is used because it is compact, it eliminates the 
 *  necessity of using parenthesis.  Polish notation is also 
 *  simple to parse.
 *  
 *  IMPORTANT NOTE:
 *  Note that the implication operator is followed by the consequent and then 
 *  the antecedent.
 *  This is backwards from the usual notation which would put the antecedent 
 *  first and then the consequent.
 *  We use this backward convention because it fits very well with the 
 *  'solver' algorithm used by this system, which must reduce antecedents 
 *  before reducing consequents.  Since we also Polish notation, and most all 
 *  formula processing (like parsing and term rewriting) starts from the end 
 *  of the formula, putting the antecedents at the end of the formula means 
 *  that antecedents will naturally be processed first.   
 *  
 *  So, to summarize...
 *  An implication is usually written thus...
 *  	a->b
 *  Using polish notation and the '*' symbol for implication the above formula 
 *  can be written like so...
 *  	*ab
 *  But since we also reverse the antecendent and the consequent we write an 
 *  implication like this...
 *  	*ba  
 *  And of cource we sue numbers for variables, so if we use a==1 and b==2 
 *  we get...
 *  	*^2^1
 *  
 *  
 *  #see PrettyFormula for converting formula to 'pretty' text. 
 *  
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
public class Formula implements Comparable<Formula> {
	
	String _symbol; // the symbol with this node; an operator, constant, or a variable
	Formula _parent; //
	
	/**
	 * Stores nodes for symbols after the symbol represented by this node.
	 * If this node is the last node in a formula then this container will be null. 
	 * Uses KEY_* for implication, negation, and constants.
	 * Uses variables numbers for everything else. 
	 */
	Map<String, SoftReference<Formula>> _children;

	/*
	 * For efficiency, hang on to formula text as long we have enough memory
	 */
	SoftReference<String> _formulaText;
	SoftReference<Formula> _antecedent;
	SoftReference<Formula> _consequent;
	
	Formula(String symbol, Formula parent) {
		_symbol= symbol;
		_parent= parent;
	}
	
	protected void finalize() throws Throwable {
		_parent._children.remove(_symbol);
		_parent= null;
	};
	
	public String getSymbol() { return _symbol; }
	
	/**
	 * @return A textual representation of this formula in normal form.
	 * @see PropositionalSystem
	 */
	public synchronized String getFormulaText() {
		String text= null;
		if (_formulaText != null && (text= _formulaText.get()) == null) {
			text= _symbol;
			Formula parent= _parent;
			while (parent != null) {
				text= parent._symbol+text;
				parent= parent._parent;
			}
			_formulaText= new SoftReference<String>(text);
		}
		return text;
	}
	
	public Formula addFormula(String formulaText) {
		if (_children == null)
			_children= new HashMap<String, SoftReference<Formula>>();
		String symbol= formulaText.substring(0, 1);
		if (symbol.equals("^")) {
			int end= 1;
			for (int i= 1; i < formulaText.length(); i++) {
				if (Character.isDigit(formulaText.charAt(i))) {
					end++;
				}
				else
					break;
			}
			symbol= formulaText.substring(0, end);
		}
		
		Formula n= getChildNode(symbol);
		if (symbol.length() < formulaText.length())
			return n.addFormula(formulaText.substring(symbol.length()));
		return n;
	}
	
	Formula getChildNode(String symbol) {
		SoftReference<Formula> ref= _children.get(symbol);
		Formula n;
		if (ref == null || (n= ref.get()) == null) {
			n= new Formula(symbol, this);
			_children.put(symbol, new SoftReference<Formula>(n));
		}
		return n;
	}

	public Iterator<Match> findMatches(String formulaText, Map<String, String> substitions) {
		String match= _symbol;
		
		// if this node is variable then get the substitution associated 
		// with the variable.
		// If there is no substitution then create one 
		if (Symbol.isVariable(_symbol)) {
			match= substitions.get(_symbol);
			if (match == null) {
				substitions= new HashMap<String, String>(substitions);
				match= PropositionalSystem.nextFormula(formulaText);
				substitions.put(_symbol, match);
			}
		}
		
		int match_length= match.length();
		int formula_length= formulaText.length();
		
		// if the formula doesn't start with the symbol associated with 
		// this node then the formula is not a match
		if (!formulaText.startsWith(match))
			return Match.EMPTY_MATCHES;
		
		// if this node represents a variable, say 25, then make sure the 
		// formula doesn't start with a variable that matches but is longer, like 255.    
		if (Symbol.isVariable(match) && match_length < formula_length && Character.isDigit(formulaText.charAt(match_length)))
			return Match.EMPTY_MATCHES;
		
		
		if (_children == null) {
			
			// this node is a leaf but there is still formula left, so not a match
			if (match_length < formula_length)
				return Match.EMPTY_MATCHES;
			
			return new SingleMatch(new Match(getFormulaText(), substitions));
		}
		
		// this node is not a leaf but were out of formula, so not a match
		if (formula_length <= match_length)
			return Match.EMPTY_MATCHES;
		
		return new CompositeMatch(_children.values().iterator(), formulaText.substring(match_length), substitions);
	}
	
	@Override
	public int compareTo(Formula formula) {
		return getFormulaText().compareTo(formula.getFormulaText());
	}

	/**
	 * @param valuation  A collection that denotes boolean values for all variables 
	 * 
	 * @return true if the given valuation satisfies this formula, else false.
	 */
	public boolean evaluate(Map<Formula, Boolean> valuation) {
		Stack<Boolean> stack= new Stack<Boolean>();
		for (Formula formula= this; formula != null; formula= formula._parent) {
			if (Symbol.isImplication(formula._symbol)) {
				Boolean consequent= stack.pop();
				Boolean antecendent= stack.pop();
				stack.push((antecendent && !consequent) ? Boolean.FALSE : Boolean.TRUE);
			}
			else if (Symbol.isVariable(formula._symbol)) {
				stack.push(valuation.get(this));
			}
			else if (Symbol.isNegation(formula._symbol)) {
				Boolean v= stack.pop();
				stack.push(v ? Boolean.FALSE : Boolean.TRUE);
			}
			else if (Symbol.isTrue(formula._symbol)) {
				stack.push(Boolean.TRUE);
			}
			else if (Symbol.isFalse(formula._symbol)) {
				stack.push(Boolean.FALSE);
			}
			else
				assert formula._symbol.equals("") : "Expected formula root, instead found "+formula._symbol;
		}
		
		assert stack.size() == 1 : "Unknown Error during evaluation";
		
		return stack.pop();
	}

	public int length() {
		return getFormulaText().length();
	}

	@Override
	public String toString() {
		return PrettyFormula.getPrettyText(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Formula) == false)
			return false;
			
		return getFormulaText().equals(((Formula)obj).getFormulaText());
	}

	public Formula getParent() {
		return _parent;
	}

	public boolean isNegation() {
		return Symbol.isNegation(getFormulaText());
	}

	public boolean isImplication() {
		return Symbol.isImplication(getFormulaText());
	}

	public Formula getAntecedent() {
		Formula formula= null;
		if (_antecedent != null && (formula= _antecedent.get()) == null) {
			text= _symbol;
			Formula parent= _parent;
			while (parent != null) {
				text= parent._symbol+text;
				parent= parent._parent;
			}
			_formulaText= new SoftReference<String>(text);
		}
		return formula;
	}


}
