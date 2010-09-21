package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Negation extends Formula {
	
	final Formula _child;

	Negation(Formula f, String text) {
		super(text);
		_child= f;
		
		// we can save memory by reusing the given text for subformula text
		_child._txt= text.substring(1);
	}
	
	public Formula getChild() {
		return _child;
	}
	
	@Override
	public boolean evaluate(Map<Variable, Boolean> valuation) {
		return _child.evaluate(valuation) ? false : true;
	}
	
	

	@Override
	public Iterator<Formula> getLeftSidedDeepestFirstIterator() {
		return new Iterator<Formula>() {
			Iterator<Formula> _rightIterator= _child.getLeftSidedDeepestFirstIterator();
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
					return Negation.this;
				}
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();					
			}
		};
	}

	@Override
	protected int subsumes(Formula formula, Map<Variable, Formula> variableBindings) {
		if (!(formula instanceof Negation))
			return 0;
		int i= _child.subsumes(((Negation)formula).getChild(), variableBindings);
		if (0 <= i)
			return i+1;
		return -1;
	}
}
