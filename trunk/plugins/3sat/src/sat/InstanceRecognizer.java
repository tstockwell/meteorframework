package sat;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.naming.OperationNotSupportedException;

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
	
	static final Integer KEY_IMP= new Integer(-1);
	static final Integer KEY_NEG= new Integer(-2);
	static final Integer KEY_TRUE= new Integer(-3);
	static final Integer KEY_FALSE= new Integer(-4);
	
	static class Match {
		Formula formula;
		Map<Integer, Formula> substitutions;
		Match (Formula formula, Map<Integer, Formula> substitutions) {
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
		Formula _formula;
		Map<Integer, Formula> _substitutions;
		public CompositeMatch(Iterator<Node> children, Formula formula, Map<Integer, Formula> substitutions) {
			_children= children;
			_formula= formula;
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
					_matches= _children.next().findMatches(_formula, _substitutions);

				}
				if (_matches.hasNext())
					return true;
				_matches= null;
			}
		}
		public void remove() { throw new UnsupportedOperationException(); }
	}
	
	
	// cache keys to save memory
	private static final HashMap<Integer, Integer> __keys= new HashMap<Integer, Integer>();
	static {
		__keys.put(KEY_IMP, KEY_IMP);
		__keys.put(KEY_NEG, KEY_NEG);
	}
	static Integer getKey(Integer key) {
		Integer k= __keys.get(key);
		if (k != null)
			return k;
		__keys.put(key, key);
		return key;
	}
	
	
	private final Node _root= new Node(null, null);
	private final PropositionalSystem _system;
	public InstanceRecognizer(PropositionalSystem system) { 
		_system= system;
	}
	
	public void addFormula(Formula formula) {
		_root.addFormula(formula);
	}
	
	/**
	 * Find all formulas for which the given formula is a substitution instance.
	 */
	public Iterator<Match> findMatches(Formula formula) {
		Map<Integer, Formula> substitions= new HashMap<Integer, Formula>();
		return _root.findMatches(formula, substitions);
	}
	
	
	
	


	class Node {
		Integer _key;
		Node _parent;
		/**
		 * Uses -1 for implication, -2 for negation, variables numbers for 
		 * everything else 
		 */
		Map<Integer, Node> _children= new HashMap<Integer, Node>();
		
		Node(Integer key, Node parent) {
			_key= getKey(key);
			_parent= parent;
		}
		
		public void addFormula(Formula formula) {
			char c= formulaText.charAt(0);
			switch (c) {
			
			case 'T':
				getNode(KEY_TRUE).addFormula(formulaText.substring(1));
				break;
				
			case 'F':
				getNode(KEY_FALSE).addFormula(formulaText.substring(1));
				break;
				
			case '*':
				getNode(KEY_IMP).addFormula(formulaText.substring(1));
				break;

			case '-':
				getNode(KEY_NEG).addFormula(formulaText.substring(1));
				break;

			case '^':
				int end= 1;
				for (int i= 1; i < formulaText.length(); i++) {
					if (Character.isDigit(formulaText.charAt(i))) {
						end++;
					}
					else
						break;
				}
				
				Integer key= Integer.parseInt(formulaText.substring(1, end));
				getNode(key).addFormula(formulaText.substring(end));
				break;
				
			default:
				throw new RuntimeException("Unknown symbol:"+c+" in formula "+formulaText);
			}
			
		}
		
		Node getNode(Integer key) {
			Node n= _children.get(key);
			if (n == null) {
				Integer k= getKey(key);
				n= new Node(k, this);
				_children.put(k, n);
			}
			return n;
		}

		public Iterator<Match> findMatches(Formula formula, Map<Integer, Formula> substitions) {
			return new CompositeMatch(_children.values().iterator(), formula, substitions);
		}
	}



	class ConstantNode extends Node {
		
		public ConstantNode(Integer key, Node parent) {
			super(key, parent);
		}

		@Override
		public Iterator<Match> findMatches(Formula formula, Map<Integer, Formula> substitions) {
			if (_key.equals(KEY_TRUE)  && formula.equals(Constant.TRUE))
				return new SingleMatch(new Match(Constant.TRUE, substitions));
			if (_key.equals(KEY_FALSE)  && formula.equals(Constant.FALSE))
				return new SingleMatch(new Match(Constant.TRUE, substitions));
			return EMPTY_MATCHES;
		}
	}



	class VariableNode extends Node {
		
		public VariableNode(Integer key, Node parent) {
			super(key, parent);
		}

		@Override
		public Iterator<Match> findMatches(Formula formula, Map<Integer, Formula> substitions) {
			Formula match= substitions.get(_key);
			if (match == null) {
				substitions= new HashMap<Integer, Formula>(substitions);
				substitions.put(_key, formula);
				return new SingleMatch(new Match(_system.createVariable(_key), substitions));
			}
			if (formula.equals(match))
				return new SingleMatch(new Match(_system.createVariable(_key), substitions));
			return EMPTY_MATCHES;
		}
	}


	class NegationNode extends Node {
		
		public NegationNode(Node parent) {
			super(KEY_NEG, parent);
		}

		@Override
		public Iterator<Match> findMatches(final Formula formula, final Map<Integer, Formula> substitions) {
			if (!(formula instanceof Negation))
				return EMPTY_MATCHES;
			return new CompositeMatch(_children.values().iterator(), formula, substitions);
		}
	}
	

	class ImplicationNode extends Node {
		
		public ImplicationNode(Node parent) {
			super(KEY_IMP, parent);
		}

		@Override
		public Iterator<Match> findMatches(final Formula formula, final Map<Integer, Formula> substitions) {
			if (!(formula instanceof Negation))
				return EMPTY_MATCHES;
			return new CompositeMatch(_children.values().iterator(), formula, substitions);
		}
	}
	
}
