package sat;

import junit.framework.TestCase;

public class Tests extends TestCase {
	
	public void testFormulaSubstitution() {
		Formula a= Formula.create("*~aF");
		Formula b= Formula.create("*aF");
		assertFalse( "Erroneously identifies *~aF as a substitution instance of *aF",
				a.isInstanceOf(b));
	}
}
