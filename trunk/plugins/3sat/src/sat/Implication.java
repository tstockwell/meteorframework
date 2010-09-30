package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Implication extends Formula {

	private final Formula _antecedent;
	private final Formula _consequent;
	
	public Implication(Formula antecedent, Formula consequent, String text) {
		super(text);
		
		// we can save memory by reusing the given text for subformula text
		int a= antecedent.length();
		(_antecedent= antecedent)._txt= text.substring(1, a+1);
		(_consequent= consequent)._txt= text.substring(a+1);
	}
	public Formula getAntecedent() {
		return _antecedent;
	}
	public Formula getConsequent() {
		return _consequent;
	}
	
	@Override
	public boolean evaluate(Map<Variable, Boolean> values) {
		return (_antecedent.evaluate(values) && !_consequent.evaluate(values)) ? false : true;
	}
	@Override
	public Iterator<Formula> getLeftSidedDeepestFirstIterator() {
		return new Iterator<Formula>() {
			Iterator<Formula> _leftIterator= _antecedent.getLeftSidedDeepestFirstIterator();
			Iterator<Formula> _rightIterator;
			int _state= 0; //0 == using left iterator, 1 == using right iterator, 2 == using this, >2 done
			public boolean hasNext() {
				if (_state == 0) {
					return _leftIterator.hasNext();
				}
				else if (_state == 1) {
					if (_rightIterator == null)
						_rightIterator= _consequent.getLeftSidedDeepestFirstIterator();
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
						_rightIterator= _consequent.getLeftSidedDeepestFirstIterator();
					Formula f= _rightIterator.next();
					if (_rightIterator.hasNext() == false)
						_state= 2;
					return f;
				}
				else if (_state == 2) {
					_state= 3;
					return Implication.this;
				}
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();					
			}
		};
	}
}
