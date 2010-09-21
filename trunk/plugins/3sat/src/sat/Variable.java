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
	public boolean evaluate(Map<Variable, Boolean> valuation) {
		return valuation.get(this);
	}

	@Override
	protected int subsumes(Formula formula, Map<Variable, Formula> variableBindings) {
		Formula match= variableBindings.get(this);
		if (match == null) {
			variableBindings.put(this, formula);
			return -1;
		}
		if (this.equals(match))
			return -1;
		return 0;
	}

}
