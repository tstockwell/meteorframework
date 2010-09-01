package sat;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * This class generates production rules for reducing propositional statements.
 * It's a long story....
 * 
 * @author Ted Stockwell
 */
public class RuleGenerator {

	public static void main(String[] args) {
		new RuleGenerator().run();
	}

	private RuleDatabase _database;
	private FormulaGenerator _formulaGenerator;
	private ServerCommandLineInterface _commandLine;

	public void run() {

		try {
			setup();
			
			Formula formula = _formulaGenerator.getStartingFormula();

			while (!_commandLine.isShutdown()) {

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
		
		ReductionRule reductionRule= null;
		if (formulaIsSoLongThatItIsGuaranteedToBeReducable(formula)) {
			System.out.println("!!!!!! The Rule Database is Complete !!!");
			_commandLine.shutDown();
		} 
		else if ((reductionRule= formulaCanBeReduced(formula)) == null) {
			boolean isCanonical= isCanonicalFormula(formula);
			if (isCanonical) {
				System.out.println(formula+" is canonical");
			}
			else 
				System.out.println(formula+" is reducable");
			formula.setCanonical(isCanonical);
			_database.addFormula(formula);
		}
		else {
			if (reductionRule.formula.length() == reductionRule.reduction.length()) {
				//System.out.println(formula+" is isomorphic to "+reductionRule.reduction);
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

	private boolean formulaIsSoLongThatItIsGuaranteedToBeReducable(Formula formula) {
		int maxLength= _database.getLengthOfLongestCanonicalFormula();
		if (maxLength <= 0) // we don't know the length of longest formula yet
			return false;
		return (maxLength*2+1) < formula.length();
	}

	private ReductionRule formulaCanBeReduced(Formula formula) {
		Iterator<Formula> formulaIterator= formula.getLeftSidedDeepestFirstIterator();
		while (formulaIterator.hasNext()) {
			Formula subformula= formulaIterator.next();
			ResultIterator<Formula> reducableFormulas = _database.getAllNonCanonicalFormulas(subformula.length());
			while (reducableFormulas.hasNext()) {
				Formula reducableFormula= reducableFormulas.next();
				if (subformula.isInstanceOf(reducableFormula))
					return new ReductionRule(subformula, reducableFormula);
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
