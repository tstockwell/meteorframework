package sat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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

	static final int MAX_CACHED_RULES= 8;
	public static List<ReductionRule> _recentReductionRules= new ArrayList<ReductionRule>(); 

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
//			if (reductionRule.formula.length() == reductionRule.reduction.length()) {
//				System.out.println("***** WARNING ****\nEncountered a reduction rule where the left and right sides have the same length");
//				System.exit(1);
//			}
//			else
				System.out.println(formula+" can be reduced using rule "+reductionRule);
		}
	}

	private Boolean isCanonicalFormula(Formula formula) {
		int length= _database.getLengthOfCanonicalFormulas(formula.getTruthTable());
		if (length < 0) // no canonical formulas in database
			return true;

		// we only return true for the FIRST formula that satisifies a given truth table
		// since we want ONLY ONE forumala to be the normal form for a given truth table.
		//return formula.length() <= length;
		return false; 
	}

	/*
	 * A formula can be reduced if it is a substitution instance of 
	 * a previously generated non-canonical formula.
	 * Since new formulas are assembled from previously generated canonical 
	 * formulas then all subformulas of the given formula are guaranteed
	 * to be non-reducable.   
	 */
	private ReductionRule formulaCanBeReduced(Formula formula) {
		
		/* an optimization...
		 * 	very often a recently used reduction rule can be used to reduce the 
		 *  current formula, so we first check the last fews rules used before 
		 *  hitting the database...
		 */
		for (int i= _recentReductionRules.size(); 0 < i--;) {
			ReductionRule rule= _recentReductionRules.get(i);
			if (formula.isInstanceOf(rule.formula)) {
				if (0 < i) {
					_recentReductionRules.remove(i);
					_recentReductionRules.add(0, rule);
				}
				return rule;
			}
		}
		
		ResultIterator<Formula> reducableFormulas = _database.getAllNonCanonicalFormulas(formula.length());
		while (reducableFormulas.hasNext()) {
			Formula reducableFormula= reducableFormulas.next();
			if (formula.isInstanceOf(reducableFormula)) { 
				ReductionRule rule= new ReductionRule(reducableFormula, _database.findCanonicalFormula(reducableFormula));
				if (MAX_CACHED_RULES < _recentReductionRules.size())
					_recentReductionRules.remove(_recentReductionRules.size()-1);
				_recentReductionRules.add(0, rule);
				return rule;
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
