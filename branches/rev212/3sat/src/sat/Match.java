package sat;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class Match {
	public String formula;
	public Map<String, String> substitutions;
	static final Iterator<Match> EMPTY_MATCHES= new Iterator<Match>() {
		public void remove() { throw new UnsupportedOperationException(); }
		public Match next() { throw new NoSuchElementException(); }
		public boolean hasNext() { return false; }
	};
	Match (String formula, Map<String, String> substitutions) {
		this.formula= formula;
		this.substitutions= substitutions;
	}
}