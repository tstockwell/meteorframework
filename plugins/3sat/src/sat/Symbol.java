package sat;

public enum Symbol {
	True("T"),
	False("F"),
	Negation("-"),
	Implication("*"),
	Variable("^");
	
	private String _text; 
	private Symbol(String text) {
		_text= text;
	}
	
	public final String getFormulaText() {
		return _text;
	}

	public static final boolean isNegation(char c) {
		return c == '-';
	}

	public static final boolean isImplication(char c) {
		return '*' == c;
	}
	
	public static final boolean isVariable(char c) {
		return '^' == c;
	}
	
	public static final boolean isTrue(char c) {
		return 'T' == c;
	}
	
	public static final boolean isFalse(char c) {
		return 'F' == c;
	}
	public static final boolean isConstant(char c) {
		return 'F' == c || 'T' == c;
	}
	

	public static final boolean isNegation(String c) {
		return c.startsWith("-");
	}

	public static final boolean isImplication(String c) {
		return c.startsWith("*");
	}
	
	public static final boolean isVariable(String c) {
		return c.startsWith("^");
	}
	
	public static final boolean isTrue(String c) {
		return c.startsWith("T");
	}
	
	public static final boolean isFalse(String c) {
		return c.startsWith("F");
	}
	public static final boolean isConstant(String c) {
		return isTrue(c) || isFalse(c);
	}
	
	
}
