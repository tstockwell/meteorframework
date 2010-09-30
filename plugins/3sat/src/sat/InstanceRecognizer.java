package sat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
public class InstanceRecognizer {
	
	static final String SYMBOL_TRUE= Constant.TRUE.getFormulaText(); 
	static final String SYMBOL_FALSE= Constant.FALSE.getFormulaText(); 
	static final String SYMBOL_NEGATION= Operator.Negation.getFormulaText(); 
	static final String SYMBOL_IMPLICATION= Operator.Implication.getFormulaText(); 
	static final String SYMBOL_VARIABLE= "^"; 
	
	
	private final Node _root= new Node("", null);
	public InstanceRecognizer() { 
	}
	public InstanceRecognizer(Collection<Formula> formulas) {
		for (Formula formula: formulas)
			addFormula(formula);
	}
	public InstanceRecognizer(Formula formula) {
		addFormula(formula);
	}
	
	public void addFormula(Formula formula) {
		_root.addFormula(formula.getFormulaText());
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
		ArrayList<Match> all= new ArrayList<InstanceRecognizer.Match>();
		while (i.hasNext())
			all.add(i.next());
		return all;
	}
	
	
	
	

	/**
	 * A node in the internal trie structure that is associated with a symbol 
	 * in a formula
	 */
	class Node {
		String _symbol; // the symbol from the associated formula associated with this node
		Node _parent;
		/**
		 * Stores nodes for symbols after the symbol represented by this node.
		 * If this node is the last node in a formula then this container will be null. 
		 * Uses KEY_* for implication, negation, and constants.
		 * Uses variables numbers for everything else. 
		 */
		Map<String, Node> _children;
		
		Node(String symbol, Node parent) {
			_symbol= symbol;
			_parent= parent;
		}
		
		String getSymbol() { return _symbol; }
		
		String getFormulaText() {
			String text= _symbol;
			Node parent= _parent;
			while (parent != null) {
				text= parent._symbol+text;
				parent= parent._parent;
			}
			return text;
		}
		
		public void addFormula(String formulaText) {
			if (_children == null)
				_children= new HashMap<String, Node>();
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
			
			Node n= getChildNode(symbol);
			if (symbol.length() < formulaText.length())
				n.addFormula(formulaText.substring(symbol.length()));
		}
		
		Node getChildNode(String symbol) {
			Node n= _children.get(symbol);
			if (n == null) {
				n= new Node(symbol, this);
				_children.put(symbol, n);
			}
			return n;
		}

		public Iterator<Match> findMatches(String formulaText, Map<String, String> substitions) {
			String match= _symbol;
			if (_symbol.startsWith(SYMBOL_VARIABLE)) {
				match= substitions.get(_symbol);
				if (match == null) {
					substitions= new HashMap<String, String>(substitions);
					match= PropositionalSystem.nextFormula(formulaText);
					substitions.put(_symbol, match);
				}
			}
			
			
			if (!formulaText.startsWith(match))
				return EMPTY_MATCHES;
			if (_children == null) {
				if (match.length() < formulaText.length())
					return EMPTY_MATCHES;
				return new SingleMatch(new Match(getFormulaText(), substitions));
			}
			if (formulaText.length() <= match.length())
				return EMPTY_MATCHES;
			return new CompositeMatch(_children.values().iterator(), formulaText.substring(match.length()), substitions);
		}
	}


	
	public static class Match {
		public String formula;
		public Map<String, String> substitutions;
		Match (String formula, Map<String, String> substitutions) {
			this.formula= formula;
			this.substitutions= substitutions;
		}
	}
	static final Iterator<Match> EMPTY_MATCHES= new Iterator<Match>() {
		public void remove() { throw new UnsupportedOperationException(); }
		public Match next() { throw new NoSuchElementException(); }
		public boolean hasNext() { return false; }
	}; 
	static class SingleMatch implements Iterator<Match> {
		Match _formula;
		public SingleMatch(Match formula) {
			_formula= formula;
		}
		public void remove() { throw new UnsupportedOperationException(); }
		public Match next() { 
			if (_formula == null)
				throw new NoSuchElementException();
			Match f= _formula;
			_formula= null;
			return f;
		}
		public boolean hasNext() { return _formula != null; }
	};
	static class CompositeMatch implements Iterator<Match> {
		Iterator<Match> _matches;
		Iterator<Node> _children;
		String _formulaText;
		Map<String, String> _substitutions;
		public CompositeMatch(Iterator<Node> children, String formulaText, Map<String, String> substitutions) {
			_children= children;
			_formulaText= formulaText;
			_substitutions= substitutions;
		}
		public Match next() {
			if (_matches == null)
				throw new NoSuchElementException();
			return _matches.next();
		}
		public boolean hasNext() {
			while(true) {
				if (_matches == null) {
					if (!_children.hasNext())
						return false;
					_matches= _children.next().findMatches(_formulaText, _substitutions);

				}
				if (_matches.hasNext())
					return true;
				_matches= null;
			}
		}
		public void remove() { throw new UnsupportedOperationException(); }
	}
	
}
