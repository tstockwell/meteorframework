package sat;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Represents a propositional formula in Polish notation using only...
 * 	...the symbols 'T' and 'F' for TRUE and FALSE.
 *  ...the symbols 'a', 'b', 'c', ...., 'z' for variables.
 *  ...the symbol '*' for the -> operator (aka the if-then operator).
 *  ...the symbol '~' for the negation operator 
 * 
 * This class cannot handle formulas with more than 26 variables so it is 
 * not useful outside the context of this package.
 * The maximum # of supported variables is configurable  
 *  
 * @author Ted Stockwell
 */
public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Can't be more than 26 because we use letters to represent variables (for readability).
	 * Should not be less than 1.
	 */
	public static final int MAX_VARIABLES= 3; 
	
	// operators
	public static final char IF_THEN= '*';
	public static final char NEGATION= '~';
	
	// constants
	public static final Formula TRUE= new Formula('T');
	public static final Formula FALSE= new Formula('F');
	
	
	transient final static private Map<String, Reference<Formula>> __cache= new TreeMap<String, Reference<Formula>>(); 
	transient final static private ReferenceQueue<Formula> __referenceQueue= new ReferenceQueue<Formula>();
	private static class FormulaReference extends WeakReference<Formula> {
		String _string;
		public FormulaReference(Formula referent) {
			super(referent, __referenceQueue);
			_string= referent._string;
		}
	}
	
	// variables
	public static final Formula[] VARIABLES= new Formula[MAX_VARIABLES];
	static {
		for (int i= 0; i < MAX_VARIABLES; i++) {
			Formula f= VARIABLES[i]= new Formula((char)('a'+i));
			__cache.put(f.getFormulaText(), new FormulaReference(f));
		}
	}
	
	TruthTable _truthTable;
	char _variable= 0;
	char _operator= 0;
	Formula _left;
	Formula _right;
	
	String _string;
	private boolean _canonical;
	
	public static Formula create(String path) {
		Formula formula= null;
		synchronized (__cache) {
			Reference<Formula> ref= __cache.get(path);
			if (ref == null || (formula= ref.get()) == null) {
				formula= parse(path);
				__cache.put(path, new FormulaReference(formula));
			}
		}
		
		// clean up expired references
		FormulaReference ref;
		while ((ref= (FormulaReference)__referenceQueue.poll()) != null) {
			synchronized (__cache) {
				__cache.remove(ref._string);
			}
		}
		
		return formula;
	}
	
	public static Formula createNegation(Formula right) {
		return create(NEGATION+right._string);
	}
	
	public static Formula createImplication(Formula antecedent, Formula consequent) {
		return create(IF_THEN+antecedent._string+consequent._string);
	}

	private static Formula parse(String formula) {
		Stack<Formula> stack= new Stack<Formula>();
		char[] symbols= formula.toCharArray();
		for (int i = symbols.length; 0 < i--;) {
			char c = symbols[i];
			if (c == NEGATION) {
				stack.push(new Formula(NEGATION, stack.pop()));
			}
			else if (c == IF_THEN) {
				stack.push(new Formula(IF_THEN, stack.pop(), stack.pop()));
			}
			else if (c == 'T') {
				stack.push(TRUE);
			}
			else if (c == 'F') {
				stack.push(FALSE);
			}
			else if ('a' <= c && c <= 'z') {
				stack.push(VARIABLES[c-'a']);
			}
			else 
				throw new RuntimeException("Unknown symbol:"+c);
		}
		
		if (stack.size() != 1) 
			throw new RuntimeException("Invalid postcondition after evaluating formula wellformedness: count < 1");
		
		Formula formula2= stack.pop();
		formula2._string= formula;
		return formula2;
	}
	
	private Formula(char operator, Formula left, Formula right) {
		if (operator != IF_THEN)
			throw new RuntimeException("Invalid operator");
		if (left == null)
			throw new IllegalArgumentException("Formula cannot be null");
		if (right == null)
			throw new IllegalArgumentException("Formula cannot be null");
		_operator= operator;
		_left= left;
		_right= right;
		_string= IF_THEN+left._string+right._string; 
	}
	private Formula(char operator, Formula right) {
		if (operator != NEGATION)
			throw new RuntimeException("Invalid operator");
		if (right == null)
			throw new IllegalArgumentException("Formula cannot be null");
		_operator= operator;
		_right= right;
		_string= NEGATION+right._string; 
	}
	private Formula(char variable) {
		_variable= variable;
		_string= ""+_variable;
	}
	
	private Object readResolve() {
		return create(_string);
	}
	
	public TruthTable getTruthTable() {
		if (_truthTable == null) {
			_truthTable= TruthTable.create(new TruthTable.Builder() {
				public boolean evaluate(String values) {
					return Formula.this.evaluate(values);
				}
			});
		}
		return _truthTable;
	}

	private boolean evaluate(String values) {
		if (_operator == IF_THEN) 
			return (_left.evaluate(values) && !_right.evaluate(values)) ? false : true;
		if (_operator == NEGATION) 
			return !_right.evaluate(values);
		if (_variable == 'T') 
			return true;
		if (_variable == 'F')
			return false;
		if (_variable != 0) 			
			return values.charAt(_variable-'a') != '0';
		throw new IllegalStateException();
	}

	public int length() {
		if (_variable != 0) 			
			return 1;
		if (_operator == NEGATION) 
			return _right.length()+1;
		if (_operator == IF_THEN) 
			return _left.length()+_right.length()+1;
		throw new IllegalStateException();
	}

	public Iterator<Formula> getLeftSidedDeepestFirstIterator() {
		if (_variable != 0) {
			ArrayList<Formula> list= new ArrayList<Formula>(1);
			list.add(this);
			return list.iterator();
		}
		
		if (_operator == NEGATION) {
			return new Iterator<Formula>() {
				Iterator<Formula> _rightIterator= _right.getLeftSidedDeepestFirstIterator();
				int _state= 0; //0 == using right iterator, 1 == using this, >1 done
				public boolean hasNext() {
					if (_state == 0) {
						return _rightIterator.hasNext();
					}
					else if (_state == 1)
						return true;
					return false;
				}

				public Formula next() {
					if (_state == 0) {
						Formula f= _rightIterator.next();
						if (_rightIterator.hasNext() == false)
							_state= 1;
						return f;
					}
					else if (_state == 1) {
						_state= 2;
						return Formula.this;
					}
					throw new NoSuchElementException();
				}

				public void remove() {
					throw new UnsupportedOperationException();					
				}
			};
		}
		
		if (_operator == IF_THEN) {
			return new Iterator<Formula>() {
				Iterator<Formula> _leftIterator= _left.getLeftSidedDeepestFirstIterator();
				Iterator<Formula> _rightIterator;
				int _state= 0; //0 == using left iterator, 1 == using right iterator, 2 == using this, >2 done
				public boolean hasNext() {
					if (_state == 0) {
						return _leftIterator.hasNext();
					}
					else if (_state == 1) {
						if (_rightIterator == null)
							_rightIterator= _right.getLeftSidedDeepestFirstIterator();
						return _rightIterator.hasNext();
					}
					else if (_state == 2)
						return true;
					return false;
				}

				public Formula next() {
					if (_state == 0) {
						Formula f= _leftIterator.next();
						if (_leftIterator.hasNext() == false)
							_state= 1;
						return f;
					}
					else if (_state == 1) {
						if (_rightIterator == null)
							_rightIterator= _right.getLeftSidedDeepestFirstIterator();
						Formula f= _rightIterator.next();
						if (_rightIterator.hasNext() == false)
							_state= 2;
						return f;
					}
					else if (_state == 2) {
						_state= 3;
						return Formula.this;
					}
					throw new NoSuchElementException();
				}

				public void remove() {
					throw new UnsupportedOperationException();					
				}
			};
		}
		throw new IllegalStateException();
	}
	
	@Override
	public String toString() {
		return getFormulaText()+"("+getTruthTable()+")";
	}
	
	public String getFormulaText() {
		if (_string == null) {
			if (_variable != 0) {
				_string= new String(new char[] { _variable });
			}
			else if (_operator == NEGATION) {
				_string= NEGATION+_right.getFormulaText();
			}
			else if (_operator == IF_THEN) { 
				_string= IF_THEN+_left.getFormulaText()+_right.getFormulaText();
			}
			else
				throw new IllegalStateException();
		}
		return _string;
	}

	public void setCanonical(boolean canonical) {
		_canonical= canonical;
	}
	public boolean isCanonical() {
		return _canonical;
	}

	/**
	 * Returns true if this formula is a substitution instance of the 
	 * given formula.
	 */
	public boolean isInstanceOf(Formula template) {
		Formula[] formulaBindings= new Formula[MAX_VARIABLES];
		return isInstanceOf(template, formulaBindings);
	}
	boolean isInstanceOf(Formula template, Formula[] formulaBindings) {
		if (template._operator == IF_THEN) {
			if (_operator != IF_THEN)
				return false;
			if (_left.isInstanceOf(template._left, formulaBindings))
				return _right.isInstanceOf(template._right, formulaBindings);
			return false;			
		}
		else if (template._operator == NEGATION) {
			if (_operator != NEGATION)
				return false;
			return _right.isInstanceOf(template._right, formulaBindings);
		}
		else if (template._variable == 'T' || template._variable == 'F') { 
			return _variable == template._variable;
		}

		int i= template._variable - 'a';
		Formula match= formulaBindings[i];
		if (match == null) {
			formulaBindings[i]= this;
			return true;
		}
		return _string.equals(match._string);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof Formula) == false)
			return false;
			
		return getFormulaText().equals(((Formula)obj).getFormulaText());
	}


}
