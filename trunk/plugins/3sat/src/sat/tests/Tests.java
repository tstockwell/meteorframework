package sat.tests;

import sat.Formula;
import sat.PropositionalSystem;
import junit.framework.TestCase;

public class Tests extends TestCase {
	
	public void testFormulas() {
		PropositionalSystem system= new PropositionalSystem();
		String text= "***#1#2#3#4";
		Formula formula= system.createFormula(text);
		assertEquals(formula.length(), text.length());
		assertEquals(formula.getFormulaText(), text);

		text= "*"+text+text;
		formula= system.createImplication(formula, formula);
		assertEquals(formula.length(), text.length());
		assertEquals(formula.getFormulaText(), text);
	}
}
