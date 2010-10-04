package sat.ruledb;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import sat.Formula;
import sat.InstanceRecognizer;
import sat.Match;
import sat.PropositionalSystem;
import sat.utils.ServerCommandLineInterface;

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

	private FormulaGenerator _formulaGenerator;
	private ServerCommandLineInterface _commandLine;
	private final PropositionalSystem _system= new PropositionalSystem();
	private final TruthTables _truthTables= new TruthTables(_system);
	private RuleDatabase _database;
	private InstanceRecognizer _recognizer= new InstanceRecognizer();
	

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
		
		_commandLine= new ServerCommandLineInterface("Rule Generator", ">>> ");
		ServerCommandLineInterface.start(_commandLine, System.in, System.out);

		_database = new RuleDatabase(_truthTables);
		_formulaGenerator= new FormulaGenerator(_database);
		
		for (Iterator<Formula> i= _database.getAllNonCanonicalFormulas(); i.hasNext();) {
			_recognizer.addFormula(i.next());
		}
	}

	private void processFormula(Formula formula) {
		
		ReductionRule reductionRule= formulaCanBeReduced(formula);
		if (reductionRule== null) {
			
			boolean isCanonical= isCanonicalFormula(formula);
			
			if (isCanonical) {
					System.out.println(formula+" is canonical.");
			}
			else {
				_recognizer.addFormula(formula);
				reductionRule= new ReductionRule(formula, _database.findCanonicalFormula(formula));
				System.out.println("Found a new reduction rule: "+reductionRule);
			}
			_database.addFormula(formula, isCanonical);
		}
		else {
			System.out.println(formula+" can be reduced using rule "+reductionRule);
		}
	}

	private Boolean isCanonicalFormula(Formula formula) {
		int length= _database.getLengthOfCanonicalFormulas(_truthTables.getTruthTable(formula));
		if (length < 0) // no canonical formulas in database
			return true;
		
		if (formula.length() <= length) {
			
//			/* 
//			 * If there are other canonical formulas with the same truth 
//			 * table and this formula is not a substitution instance of any 
//			 * of those other canonical formulas then we make this formula 
//			 * a reduction rule.
//			 * Doing this reduces the number of canonical formulas and greatly 
//			 * reduces the total number of reduction rules. 
//			 * We only do this for formulas that are not substitution instances 
//			 * of existing canonical formula in order to preserve the confluence 
//			 * of the reduction rules.
//			 */
//			List<Formula> canonicalFormulas= _database.getCanonicalFormulas(_truthTables.getTruthTable(formula));
//			if (!canonicalFormulas.isEmpty()) 
//				if (new InstanceRecognizer(canonicalFormulas).findFirstMatch(formula) == null)  
//					return false;
			
			return true;
		}
		
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
		Match match= _recognizer.findFirstMatch(formula);
		if (match == null)
			return null;
		Formula reducableFormula= _system.createFormula(match.formula);
		Formula canonicalFormula= _database.findCanonicalFormula(reducableFormula);
		return new ReductionRule(reducableFormula, canonicalFormula);
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
