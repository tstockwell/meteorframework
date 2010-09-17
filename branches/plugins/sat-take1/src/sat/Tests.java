package sat;

import junit.framework.TestCase;

public class Tests extends TestCase {
	public void testUnification() {
		Formula A= Formula.create("*~aa");
		Formula B= Formula.create("**abc");
		Substitution s= B.unify(A);
		System.out.print(s);
		s= B.getAntecedent().unify(A);
		System.out.print(s);
	}
}
