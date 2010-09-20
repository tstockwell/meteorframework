package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

abstract public class Constant extends Formula {
	// constants
	public static final Formula TRUE= new Constant("T") {
		final boolean evaluate(Map<String, Boolean> valuation) {
			return true;
		};
	};
	public static final Formula FALSE= new Constant("F") {
		final boolean evaluate(Map<String, Boolean> valuation) {
			return false;
		};
	};

	private Constant(String text) {
		super(text);
	}
	
	@Override
	public Iterator<Formula> getLeftSidedDeepestFirstIterator() {
		return new Iterator<Formula>() {
			boolean _first= true;
			public Formula next() {
				if (_first) {
					_first= false;
					return Constant.this;
				}
				throw new NoSuchElementException();
			}
			public boolean hasNext() { return _first; }
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}
	final protected int isInstanceOf(Formula template, Map<String, Formula> formulaBindings) {
		return template._txt.equals(_txt) ? -1 : 0;
	}
}