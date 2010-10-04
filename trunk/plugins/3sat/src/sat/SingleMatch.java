package sat;

import java.util.Iterator;
import java.util.NoSuchElementException;

class SingleMatch implements Iterator<Match> {
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
}