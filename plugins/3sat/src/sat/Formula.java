package sat;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a propositional formula
 * 
 * A Formula is really a leaf node in a trie that is used to store a set of 
 * Formulas.
 * The leaf nodes have strong references to thier parents and soft references 
 * to their children.
 * If some other object does not hold a string reference to a leaf formula
 * then the Formula will be garbage collected. 
 * When a Formula is finalized it will remove itself from the trie, 
 * thus making it possible for parent nodes to also be garbage collected.   
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
public class Formula implements Comparable<Formula> {
	
	String _symbol; // the symbol with this node; an operator, constant, or a variable
	Formula _parent;
	
	/**
	 * Stores nodes for symbols after the symbol represented by this node.
	 * If this node is the last node in a formula then this container will be null. 
	 * Uses KEY_* for implication, negation, and constants.
	 * Uses variables numbers for everything else. 
	 */
	Map<String, SoftReference<Formula>> _children;
	
	
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
	public String getFormulaText() {
		String text= _symbol;
		Formula parent= _parent;
		while (parent != null) {
			text= parent._symbol+text;
			parent= parent._parent;
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
		for (Formula parent= _parent; parent != null; parent= parent._parent) {
			if (Symbol.isImplication(_symbol)) {
				Boolean antecendent= stack.pop();
				Boolean consequent= stack.pop();
				stack.push((antecendent && !consequent) ? Boolean.FALSE : Boolean.TRUE);
			}
			else if (Symbol.isVariable(_symbol)) {
				stack.push(valuation.get(this));
			}
			else if (Symbol.isNegation(_symbol)) {
				Boolean v= stack.pop();
				stack.push(v ? Boolean.FALSE : Boolean.TRUE);
			}
			else if (Symbol.isTrue(_symbol)) {
				stack.push(Boolean.TRUE);
			}
			else if (Symbol.isFalse(_symbol)) {
				stack.push(Boolean.FALSE);
			}
			else
				throw new RuntimeException("Unknown symbol:"+_symbol);

		}
		return stack.pop();
	}

	public int length() {
		return _symbol.length() + (_parent == null ? 0 : _parent.length());
	}

	@Override
	public String toString() {
		return getFormulaText();
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
