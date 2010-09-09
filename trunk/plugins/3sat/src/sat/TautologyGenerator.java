package sat;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * This class generates all tautological formulas.
 * @author Ted Stockwell
 */
public class TautologyGenerator {

	public static void main(String[] args) {
		new TautologyGenerator().run();
	}

	private RuleDatabase _database;
	private FormulaGenerator _formulaGenerator;
	private ServerCommandLineInterface _commandLine;

	public void run() {

		try {
			setup();
			
			Formula formula = _formulaGenerator.getStartingFormula();

			while (formula != null && !_commandLine.isShutdown()) {

				processFormula(formula);
				formula = _formulaGenerator.getNextWellFormedFormula();
			}

		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			shutdown();
		}

	}

	private void setup() throws SQLException {
		addShutdownHook();

		_database = new RuleDatabase();
		_formulaGenerator= new FormulaGenerator(_database);
		
		_commandLine= new ServerCommandLineInterface("Rule Generator", ">>> ");
		ServerCommandLineInterface.start(_commandLine, System.in, System.out);
	}

	private void processFormula(Formula formula) {
		
		ReductionRule reductionRule= formulaCanBeReduced(formula);
		if (reductionRule== null) {
			boolean isCanonical= isCanonicalFormula(formula);
			if (isCanonical) {
				System.out.println(formula+" is canonical.");
			}
			else {
				reductionRule= new ReductionRule(formula, _database.findCanonicalFormula(formula));
				System.out.println("Found a new reduction rule: "+reductionRule);
			}
			formula.setCanonical(isCanonical);
			_database.addFormula(formula);
		}
		else {
			if (reductionRule.formula.length() == reductionRule.reduction.length()) {
				System.out.println("***** WARNING ****\nEncountered a reduction rule where the left and right sides have the same length");
				System.exit(1);
			}
			else
				System.out.println(formula+" can be reduced using rule "+reductionRule);
		}
	}

	private Boolean isCanonicalFormula(Formula formula) {
		int length= _database.getLengthOfCanonicalFormulas(formula.getTruthTable());
		if (length < 0) // no canonical formulas in database
			return true;
		return formula.length() <= length; 
	}

	/*
	 * A formula can be reduced if it contains a substitution instance of 
	 * a previously generated non-canonical formula.
	 */
	private ReductionRule formulaCanBeReduced(Formula formula) {
		Iterator<Formula> formulaIterator= formula.getLeftSidedDeepestFirstIterator();
		while (formulaIterator.hasNext()) {
			Formula subformula= formulaIterator.next();
			ResultIterator<Formula> reducableFormulas = _database.getAllNonCanonicalFormulas(subformula.length());
			while (reducableFormulas.hasNext()) {
				Formula reducableFormula= reducableFormulas.next();
				if (subformula.isInstanceOf(reducableFormula)) 
					return new ReductionRule(reducableFormula, _database.findCanonicalFormula(reducableFormula));
			}
		}
		return null;
	}

	private void shutdown() {
		_database.shutdown();
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				_commandLine.shutDown();
			}
		});
	}

}
