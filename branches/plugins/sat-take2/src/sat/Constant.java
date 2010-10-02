package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

abstract public class Constant extends Formula {
	// constants
	public static final Formula TRUE= new Constant("T") {
		public final boolean evaluate(Map<Variable, Boolean> valuation) {
			return true;
		};
	};
	public static final Formula FALSE= new Constant("F") {
		public final boolean evaluate(Map<Variable, Boolean> valuation) {
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
	final protected int findSubstutions(Formula formula, Map<Variable, Formula> variableBindings) {
		return formula._txt.equals(_txt) ? -1 : 0;
	}
}
