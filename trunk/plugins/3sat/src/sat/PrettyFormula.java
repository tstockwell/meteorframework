package sat;

import java.util.Stack;

/**
 * A utility for converting 'pretty' formula text into this system's 
 * normal form, and vice versa.
 * 
 * @author Ted Stockwell
 */
public class PrettyFormula {
	private static final PropositionalSystem __system= new PropositionalSystem();


	public static String getFormulaText(String prettyText) {
		prettyText= prettyText.trim();
		if (prettyText.startsWith("(")) {
			String antecedent= getFormulaText(prettyText.substring(1));
			int o= prettyText.indexOf("->", 1+antecedent.length());
			String consequent= getFormulaText(prettyText.substring(o+2));
			return Operator.Implication.getFormulaText()+consequent+antecedent;
		}
		if (prettyText.startsWith("~")) {
			String antecedent= getFormulaText(prettyText.substring(1));
			return Operator.Negation.getFormulaText()+antecedent;
		}
		if (prettyText.startsWith("^")) {
			int i= 1;
			while (Character.isDigit(prettyText.charAt(i))) 
				i++;
			return Symbol.Variable.getFormulaText()+prettyText.substring(1, i);
		}
		if (prettyText.startsWith("T")) 
			return Symbol.True.getFormulaText();
		if (prettyText.startsWith("F")) 
			return Symbol.False.getFormulaText();
		throw new RuntimeException("Cannot parse "+prettyText);
	}

	public static String getPrettyText(String formula) {
		return getPrettyText(__system.createFormula(formula));
	}

	public static String getPrettyText(Formula formula) {
		Stack<String> stack= new Stack<String>();
		for (; formula != null; formula= formula._parent) {
			if (Symbol.isImplication(formula._symbol)) {
				String consequent= stack.pop();
				String antecendent= stack.pop();
				stack.push("("+antecendent+"->"+consequent+")");
			}
			else if (Symbol.isVariable(formula._symbol)) {
				stack.push(formula._symbol);
			}
			else if (Symbol.isNegation(formula._symbol)) {
				stack.push("-"+stack.pop());
			}
			else if (Symbol.isTrue(formula._symbol)) {
				stack.push("T");
			}
			else if (Symbol.isFalse(formula._symbol)) {
				stack.push("F");
			}
			else
				assert formula._symbol.equals("") : "Expected formula root, instead found "+formula._symbol;
		}

		assert stack.size() == 1 : "Unknown Error during evaluation";

		return stack.pop();
	}


}
