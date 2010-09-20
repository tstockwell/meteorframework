package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Variable extends Formula {
	
	public Variable(String text) {
		super(text);
	}

	@Override
	public Iterator<Formula> getLeftSidedDeepestFirstIterator() {
		return new Iterator<Formula>() {
			boolean _first= true;
			public Formula next() {
				if (_first) {
					_first= false;
					return Variable.this;
				}
				throw new NoSuchElementException();
			}
			public boolean hasNext() { return _first; }
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}

	@Override
	public boolean evaluate(Map<String, Boolean> valuation) {
		return valuation.get(_txt);
	}

	@Override
	protected int isInstanceOf(Formula template, Map<String, Formula> formulaBindings) {
		Formula match= formulaBindings.get(_txt);
		if (match == null) {
			formulaBindings.put(_txt, this);
			return -1;
		}
		if (_txt.equals(match._txt))
			return -1;
		return 0;
	}

}
