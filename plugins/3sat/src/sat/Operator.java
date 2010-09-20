package sat;

public enum Operator {
	Negation('~'),
	Implication('*');
	
	private char _text; 
	private Operator(char text) {
		_text= text;
	}
	
	public char asCharacter() {
		return _text;
	}

	public static final boolean isNegation(char c) {
		return c == '~';
	}

	public static final boolean isImplication(char c) {
		return '*' == c;
	}
}