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

import sat.ruledb.TruthTable;
import sat.utils.Resource;

/**
 * Represents a propositional formula using only...
 * 	...TRUE and FALSE.
 *  ...variables.
 *  ...a negation operator 
 *  ...an implication operator (aka the if-then operator, most often written as ->).
 *  
 *  Use the static Formula.create* methods to create instances of formulas.
 * 
 * @author Ted Stockwell
 */
public class Formula extends Resource {
	
	public static enum Operator {
		Negation,
		Implication
	}
	private String _string;
	
	private int _variable= 0;
	private Operator _operator= null;
	private Formula _left;
	private Formula _right;
	// the length of the formula assuming that length of variables is 1.
	private int _length; 
	
	
	
	private static Formula parse(String formula) {
		Stack<Formula> stack= new Stack<Formula>();
		char[] symbols= formula.toCharArray();
		for (int i = symbols.length; 0 < i--;) {
			char c = symbols[i];
			if (c == Operator.Negation) {
				stack.push(new Formula(Operator.Negation, stack.pop()));
			}
			else if (c == Operator.Implication) {
				stack.push(new Formula(Operator.Implication, stack.pop(), stack.pop()));
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

	// create an implication
	Formula(Formula left, Formula right) {
		if (left == null)
			throw new IllegalArgumentException("Formula cannot be null");
		if (right == null)
			throw new IllegalArgumentException("Formula cannot be null");
		_operator= Operator.Implication;
		_left= left;
		_right= right;
		_string= Operator.Implication+left._string+right._string; 
		_length= left.length()+right.length()+1;
	}
	// create a negation
	Formula(Formula right) {
		if (right == null)
			throw new IllegalArgumentException("Formula cannot be null");
		_operator= Operator.Negation;
		_right= right;
		_string= Operator.Negation+right._string; 
		_length= right.length()+1;
	}
	Formula(int variable) {
		_variable= variable;
		_string= ""+_variable;
		_length= 1;
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
		if (_operator == Operator.Implication) 
			return (_left.evaluate(values) && !_right.evaluate(values)) ? false : true;
		if (_operator == Operator.Negation) 
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
		return _length;
	}

	/**
	 * Returns an iterator that enumerates all the subformulas of this 
	 * formula, always enumerating the subformulas of antecedents before 
	 * the subformulas of consequences.
	 * This formula is the last formula returned.  
	 */
	public Iterator<Formula> getLeftSidedDeepestFirstIterator() {
		if (_variable != 0) {
			ArrayList<Formula> list= new ArrayList<Formula>(1);
			list.add(this);
			return list.iterator();
		}
		
		if (_operator == Operator.Negation) {
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
		
		if (_operator == Operator.Implication) {
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
			else if (_operator == Operator.Negation) {
				_string= Operator.Negation+_right.getFormulaText();
			}
			else if (_operator == Operator.Implication) { 
				_string= Operator.Implication+_left.getFormulaText()+_right.getFormulaText();
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
	 * @return -1 if this formula is a substitution instance of the given formula.
	 * 	Otherwise return the position from the left in the given template formula where matching failed.
	 */
	public int isInstanceOf(Formula template) {
		Formula[] formulaBindings= new Formula[Domain.variableCount()];
		return isInstanceOf(template, formulaBindings);
	}
	private int isInstanceOf(Formula template, Formula[] formulaBindings) {
		if (template._operator == Operator.Implication) {
			if (_operator != Operator.Implication)
				return 0;
			int i= _left.isInstanceOf(template._left, formulaBindings);
			if (0 <= i)
				return i + 1;
			i= _right.isInstanceOf(template._right, formulaBindings);
			if (0 <= i)
				return template._left.length()+i+1;
			return -1;
		}
		else if (template._operator == Operator.Negation) {
			if (_operator != Operator.Negation)
				return 0;
			int i= _right.isInstanceOf(template._right, formulaBindings);
			if (0 <= i)
				return i+1;
			return -1;
		}
		else if (template._variable == 'T' || template._variable == 'F') { 
			if (_variable == template._variable)
				return -1;
			return 0;
		}

		int i= template._variable - 'a';
		Formula match= formulaBindings[i];
		if (match == null) {
			formulaBindings[i]= this;
			return -1;
		}
		if (_string.equals(match._string))
			return -1;
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj instanceof Formula) == false)
			return false;
			
		return getFormulaText().equals(((Formula)obj).getFormulaText());
	}


	public Formula getAntecedent() {
		return _left;
	}

	public Formula getConsequence() {
		return _right;
	}

}
