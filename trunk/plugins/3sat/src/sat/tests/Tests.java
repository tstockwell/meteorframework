package sat.tests;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import sat.Formula;
import sat.Implication;
import sat.PropositionalSystem;
import sat.Variable;

public class Tests extends TestCase {
	
	public void testFormulaConstruction() {
		PropositionalSystem system= new PropositionalSystem();
		String text= "***#1#2#3#4";
		Formula formula1= system.createFormula(text);
		assertEquals(formula1.length(), text.length());
		assertEquals(formula1.getFormulaText(), text);

		text= "*"+text+text;
		Formula formula2= system.createImplication(formula1, formula1);
		assertEquals(formula2.length(), text.length());
		assertEquals(formula2.getFormulaText(), text);

		text= "~"+text;
		Formula formula3= system.createNegation(formula2);
		assertEquals(formula3.length(), text.length());
		assertEquals(formula3.getFormulaText(), text);
		
		Formula formula4= system.createVariable(23);
		assertEquals(formula4.getFormulaText(), "#23");
		
		
	}
	
	public void testSubsumption() {
		PropositionalSystem system= new PropositionalSystem();
		String text= "***#1#2#3#4";
		Formula formula1= system.createFormula(text);

		text= "*"+text+text;
		Formula formula2= system.createImplication(formula1, formula1);
		assertEquals(-1, system.createFormula("*#1#2").subsumes(formula2));
		assertEquals(-1, system.createFormula("*#1#1").subsumes(formula2));
		assertEquals(-1, system.createFormula("**#1#2*#3#4").subsumes(formula2));
		assertEquals(-1, system.createFormula("**#1#2*#3#2").subsumes(formula2));
		assertEquals(-1, system.createFormula("**#1#2*#1#4").subsumes(formula2));
		assertEquals(-1, system.createFormula("**#1#2*#1#2").subsumes(formula2));

		text= "~"+text;
		Formula formula3= system.createNegation(formula2);
		assertEquals(-1, system.createFormula("~#1").subsumes(formula3));
	}
	
	public void testEvaluation() {
		PropositionalSystem system= new PropositionalSystem();
		Variable one= system.createVariable(1);
		Variable two= system.createVariable(2);
		Implication formula1= system.createImplication(one, two);
		Formula formula2= system.createNegation(formula1);
		for (int a= 0; a <= 1; a++) {
			for (int b= 0; b <= 1; b++) {
				Map<Variable, Boolean> valuation= new HashMap<Variable, Boolean>();
				valuation.put(one, (a == 1 ? Boolean.TRUE : Boolean.FALSE));
				valuation.put(two, (b == 1 ? Boolean.TRUE : Boolean.FALSE));
				boolean value1= formula1.evaluate(valuation);
				assertEquals("formula evaluation failed", a != 1 || b != 0, value1);
				boolean value2= formula2.evaluate(valuation);
				assertEquals("formula evaluation failed", !value1, value2);
			}
		}
	}
}
