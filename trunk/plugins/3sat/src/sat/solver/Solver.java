package sat.solver;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import sat.Formula;
import sat.InstanceRecognizer;
import sat.Match;
import sat.PropositionalSystem;
import sat.Symbol;
import sat.ruledb.RuleDatabase;
import sat.ruledb.TruthTables;
import sat.utils.ArgUtils;

/**
 * A tool for determining the satisfiability of boolean expressions 
 * using the reduction rule database generated by sat.ruledb.RuleGenerator.
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
public class Solver {
	
	public static final Solver FAUX_SOLVER= new Solver() {
		public final Formula reduce(Formula formula) {
			return formula;
		};
	};
	
	public static void main(String[] args) throws IOException, SQLException {
		PropositionalSystem system= new PropositionalSystem();
		TruthTables truthTables= new TruthTables(system);
		String file= ArgUtils.getString(args, "file", true);
		CNFFile cnf= CNFFile.read(system, new FileInputStream(file));
		
		Solver solver= new Solver(system, new RuleDatabase(truthTables));
		Formula reducedForm= solver.reduce(cnf.getFormula());
		
		// produce output according to rules here:
		// 	http://www.satcompetition.org/2004/format-solvers2004.html
		int status;
		if (system.getFalse().equals(reducedForm)) {
			System.out.println("s UNSATISFIABLE");
			status= 20;
		}
		else {
			System.out.println("s SATISFIABLE");
			printSatisfyingValuation(reducedForm);
			status= 10;
		}
		System.exit(status);
	}
	
	final private PropositionalSystem _system;
	final private RuleDatabase _ruleDatabase; 
	final private InstanceRecognizer _recognizer= new InstanceRecognizer();
	
	/*
	 * cache of reduction results.
	 * Used to avoid repeatedly reducing the same formula
	 */
	
	/*
	 * For efficiency we cache formula reductions.
	 */
	final Map<Integer, Formula> _reductionCache= new HashMap<Integer, Formula>(); 
	final private ReferenceQueue<Formula> _referenceQueue= new ReferenceQueue<Formula>();
	private class FormulaReference extends SoftReference<Formula> {
		int _hash;
		public FormulaReference(Formula referent) {
			super(referent, _referenceQueue);
			_hash= referent.hashCode();
		}
		@Override
		public int hashCode() {
			return _hash;
		}
	}
	
	
	public Solver(PropositionalSystem system, RuleDatabase ruleDatabase) { 
		_system= system;
		_ruleDatabase= ruleDatabase;
		for (Iterator<Formula> i= _ruleDatabase.getAllNonCanonicalFormulas(); i.hasNext();) 
			_recognizer.addFormula(i.next());
	}
	
	private Solver() { 
		_system= null;
		_ruleDatabase= null;
	}
	
	

	private static void printSatisfyingValuation(Formula reducedForm) {
		throw new RuntimeException("Unfinished");
		
	}

	public Formula reduce(Formula formula) {
		
		while (true) {
			Formula cached= checkCache(formula);
			if (cached != null)
				return cached;
			
			
			//
			// reduce subformulas before reducing this formula
			//
			String formulaText= formula.getFormulaText();
			if (formula.isNegation()) { 
				Formula negated= formula.getAntecedent();
				Formula n= reduce(negated);
				if (negated != n)
					formula= _system.createNegation(n);
			}
			else if (formula.isImplication()) {
				Formula antecent= formula.getAntecedent();
				Formula consequent= formula.getConsequent();
				Formula a= reduce(antecent);
				if (a != antecent) {
					formula= _system.createImplication(consequent, a);
				}
				else {
					Formula c= reduce(consequent);
					if (c != consequent)
						formula= _system.createImplication(c, antecent);
				}
			}
			
			Formula reduced= applyRules(formula);
			if (reduced == formula)
				return formula; // formula not reduced, no more to do
			formula= reduced; continue; // formula reduced, go around again
		}
	}



	/**
	 * Only applies rules to the given formula, not subformulas
	 * @returns the reduced rule if a rule applied, else the given rule.
	 */
	private Formula applyRules(Formula formula) {
		Formula cached= checkCache(formula);
		if (cached != null)
			return cached;
		
		Formula reducedFormula= formula;
		Match match= _recognizer.findFirstMatch(formula);
		if (match != null) {
			Formula rule= _system.createFormula(match.formula);
			Formula canonicalForm= _ruleDatabase.findCanonicalFormula(rule);
			HashMap<Formula, Formula> substitutions= new HashMap<Formula, Formula>(match.substitutions.size());
			for (String v: match.substitutions.keySet()) {
				Formula variable= _system.createFormula(v);
				Formula substitution= _system.createFormula(match.substitutions.get(v));
				substitutions.put(variable, substitution);
			}
			reducedFormula= _system.createInstance(canonicalForm, substitutions);
		}
		
		addToCache(formula, reducedFormula);
		
		return reducedFormula;
	}
	
	private synchronized Formula checkCache(Formula formula) {
		try {
			return _reductionCache.get(formula.hashCode());
		}
		finally {
			cleanCache();
		}
	}
	
	private synchronized void addToCache(Formula formula, Formula reduction) {
		new FormulaReference(formula);
		_reductionCache.put(formula.hashCode(), reduction);
		cleanCache();
	}
	private synchronized void cleanCache() {
		// clean up expired references
		FormulaReference ref;
		while ((ref= (FormulaReference)_referenceQueue.poll()) != null) {
			_reductionCache.remove(ref.hashCode());
		}
	}
}
