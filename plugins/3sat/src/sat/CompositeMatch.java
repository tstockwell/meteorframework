package sat;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


class CompositeMatch implements Iterator<Match> {
	Iterator<Match> _matches;
	Iterator<SoftReference<Formula>> _children;
	String _formulaText;
	Map<String, String> _substitutions;
	public CompositeMatch(Iterator<SoftReference<Formula>> children, String formulaText, Map<String, String> substitutions) {
		_children= children;
		_formulaText= formulaText;
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
				Formula formula= _children.next().get();
				if (formula != null)
					_matches= formula.findMatches(_formulaText, _substitutions);

			}
			if (_matches.hasNext())
				return true;
			_matches= null;
		}
	}
	public void remove() { throw new UnsupportedOperationException(); }
}