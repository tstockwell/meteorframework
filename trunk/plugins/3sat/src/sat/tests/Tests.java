package sat.tests;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import sat.Constant;
import sat.Formula;
import sat.Implication;
import sat.InstanceRecognizer;
import sat.Match;
import sat.PropositionalSystem;
import sat.Variable;
import sat.ruledb.RuleDatabase;
import sat.ruledb.TruthTable;
import sat.ruledb.TruthTables;
import sat.solver.CNFFile;
import sat.solver.Solver;

public class Tests extends TestCase {

	public void testFormulaConstruction() {
		PropositionalSystem system = new PropositionalSystem();

		assertEquals("^1", system.createVariable(1).getFormulaText());

		assertEquals("*^1^1", system.createFormula("*^1^1").getFormulaText());

		String text = "***^1^2^3^4";
		Formula formula1 = system.createFormula(text);
		assertEquals(formula1.length(), text.length());
		assertEquals(formula1.getFormulaText(), text);

		text = "*" + text + text;
		Formula formula2 = system.createImplication(formula1, formula1);
		assertEquals(formula2.length(), text.length());
		assertEquals(formula2.getFormulaText(), text);

		text = "-" + text;
		Formula formula3 = system.createNegation(formula2);
		assertEquals(formula3.length(), text.length());
		assertEquals(formula3.getFormulaText(), text);

		Formula formula4 = system.createVariable(23);
		assertEquals(formula4.getFormulaText(), "^23");

	}

	public void testEvaluation() {
		PropositionalSystem system = new PropositionalSystem();
		Variable one = system.createVariable(1);
		Variable two = system.createVariable(2);
		Implication formula1 = system.createImplication(one, two);
		Formula formula2 = system.createNegation(formula1);
		for (int a = 0; a <= 1; a++) {
			for (int b = 0; b <= 1; b++) {
				Map<Variable, Boolean> valuation = new HashMap<Variable, Boolean>();
				valuation.put(one, (a == 1 ? Boolean.TRUE : Boolean.FALSE));
				valuation.put(two, (b == 1 ? Boolean.TRUE : Boolean.FALSE));
				boolean value1 = formula1.evaluate(valuation);
				assertEquals("formula evaluation failed", a != 1 || b != 0,
						value1);
				boolean value2 = formula2.evaluate(valuation);
				assertEquals("formula evaluation failed", !value1, value2);
			}
		}
	}

	public void testTruthTables() {
		PropositionalSystem system = new PropositionalSystem();
		Formula formula = system.createFormula("-^1");
		TruthTables tables = new TruthTables(system);
		TruthTable truthTable = tables.getTruthTable(formula);
		assertTrue(truthTable.toString().startsWith("1010"));

		formula = system.createFormula("*^1^2");
		truthTable = tables.getTruthTable(formula);
		assertTrue(truthTable.toString().startsWith("1011"));

		formula = system.createFormula("-**^1-^2-^3");
		truthTable = tables.getTruthTable(formula);
		assertTrue(truthTable.toString().startsWith("00001110"));

		// **^1^2-*^1^3 ==> -**^2-^3-^1

		formula = system.createFormula("**^3^2-*^3^1");
		truthTable = tables.getTruthTable(formula);
		assertTrue(truthTable.toString().startsWith("00001110"));
	}

	public void testSubstitutions() {
		PropositionalSystem system = new PropositionalSystem();
		String text = "***^1^2^3^4";
		Formula formula1 = system.createFormula(text);

		HashMap<Variable, Formula> substitutions = new HashMap<Variable, Formula>();
		substitutions.put(system.createVariable(1), system.createVariable(5));
		substitutions.put(system.createVariable(2), system.createVariable(6));
		substitutions.put(system.createVariable(3), system.createVariable(7));
		substitutions.put(system.createVariable(4), system.createVariable(8));
		Formula instance = system.createFormula(formula1, substitutions);

		assertEquals("***^5^6^7^8", instance.getFormulaText());

		Formula instance2 = system.createFormula("***^5^6^7^8");
		assertSame(instance2, instance);
	}

	public void testRecognizer() {
		PropositionalSystem system = new PropositionalSystem();

		String text = "***^1^2^3^4";
		Formula formula1 = system.createFormula(text);
		text = "*" + text + text;
		Formula formula2 = system.createImplication(formula1, formula1);

		InstanceRecognizer recognizer = new InstanceRecognizer();
		Formula rule1 = system.createFormula("*^1^2");
		recognizer.addFormula(rule1);
		assertEquals(1, recognizer.findAllMatches(rule1).size());
		assertEquals(1, recognizer.findAllMatches(formula2).size());
		Match match = recognizer.findFirstMatch(formula2);
		assertNotNull(match.substitutions);
		assertEquals(2, match.substitutions.size());

		recognizer.addFormula(system.createFormula("*^1^1"));
		assertEquals(2, recognizer.findAllMatches(formula2).size());
		recognizer.addFormula(system.createFormula("**^1^2*^3^4"));
		assertEquals(3, recognizer.findAllMatches(formula2).size());
		recognizer.addFormula(system.createFormula("**^1^2*^3^2"));
		assertEquals(4, recognizer.findAllMatches(formula2).size());
		recognizer.addFormula(system.createFormula("**^1^2*^1^4"));
		assertEquals(5, recognizer.findAllMatches(formula2).size());
		recognizer.addFormula(system.createFormula("**^1^2*^1^2"));
		assertEquals(6, recognizer.findAllMatches(formula2).size());

		Formula formula3 = system.createNegation(formula2);
		recognizer.addFormula(system.createFormula("-^1"));
		assertEquals(1, recognizer.findAllMatches(formula3).size());

	}

	
	public void testSolver() throws SQLException, IOException {

		ClassLoader classLoader= getClass().getClassLoader();
		String homeFolder= getClass().getPackage().getName().replaceAll("\\.","/");
		PropositionalSystem system= new PropositionalSystem();
		TruthTables truthTables= new TruthTables(system);
		RuleDatabase ruleDatabase= new RuleDatabase(truthTables);
		Solver solver= new Solver(system, ruleDatabase);

		InputStream inputStream= classLoader.getResourceAsStream(homeFolder+"/cnf-example-1.txt");
		assertNotNull("Missing input file:"+homeFolder+"/cnf-example-1.txt", inputStream);
		CNFFile file= CNFFile.readAndReduce(system, inputStream, solver);
		Formula formula= file.getFormula();
		assertEquals(truthTables.getTruthTable(formula), truthTables.getTruthTable(Constant.FALSE));
		assertEquals(Constant.FALSE, formula);
		inputStream.close();

		inputStream= classLoader.getResourceAsStream(homeFolder+"/SAT_dat.k45.txt");
		assertNotNull("Missing input file:"+homeFolder+"/SAT_dat.k45.txt", inputStream);
		file= CNFFile.readAndReduce(system, inputStream, solver);
		assertEquals(Constant.FALSE, file.getFormula());
		inputStream.close();
	}
}
