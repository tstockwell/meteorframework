package sat;

public enum Operator {
	Negation("-"),
	Implication("*");
	
	private String _text; 
	private Operator(String text) {
		_text= text;
	}
	
	public String getFormulaText() {
		return _text;
	}

	public static final boolean isNegation(char c) {
		return c == '-';
	}

	public static final boolean isImplication(char c) {
		return '*' == c;
	}
}