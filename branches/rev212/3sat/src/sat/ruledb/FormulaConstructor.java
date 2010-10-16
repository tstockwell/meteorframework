package sat.ruledb;

import java.util.NoSuchElementException;

import sat.Formula;
import sat.PropositionalSystem;

/**
 * Creates new formulas of a specified length by assembling 
 * the new formulas using previously created canonical formulas in the 
 * database.
 * Let L be the specified length. 
 * This constructor only generates new formulas from smaller canonical formulas,
 * that is, it does not create new formulas suing smaller formulas that we already 
 * know are reducable.  
 * 
 * It does this by first creating all possible formulas of length L 
 * that begin with Formula.NEGATION by retrieving all canonical formulas 
 * of L-1 from the formula database and prepending them with
 * Formula.NEGATION.
 * 
 * Then it creates all possible formulas beginning with Formula.IF_THEN 
 * by iterating the length of the 'right-side' formula from L-2 to 1.
 * Let R be the length of the right-side formulas 
 * then 
 * 		Retrieve all canonical formulas of length R from the database.
 * 		Let the number of all such formulas be RCOUNT. 
 * 		Retrieve all canonical formulas of length L - R - 1 from the database.
 * 		Let the number of all such formulas be LCOUNT.
 * 		Create LCOUNT*RCOUNT new formulas by prepending Formula.IF_THEN 
 * 		to all possible combinations of right-side formula and left-side formula. 
 */
public class FormulaConstructor implements ResultIterator<Formula> {

	RuleDatabase _database;
	int _formulaLength;

	NegationFormulaConstructor _negationConstructor= null;
	IfThenFormulaConstructor _ifthenConstructor= null;
	int _rightLength;
	final PropositionalSystem _system;
	
	
	/**
	 * Creates a FormulaConstructor that will bypass formulas 
	 * until after if finds the given starting formula.
	 * @param startingFormula
	 */
	public FormulaConstructor(RuleDatabase database, Formula startingFormula) {
		this(database, startingFormula, startingFormula.length());
	}

	public FormulaConstructor(RuleDatabase database, int formulaLength) {
		this(database, null, formulaLength);
	}
	private FormulaConstructor(RuleDatabase database, Formula startingFormula, int formulaLength) {
		
		_database= database;
		_formulaLength= formulaLength;
		_negationConstructor= new NegationFormulaConstructor();
		_rightLength= formulaLength - 2;
		_system= database.getSystem();
		
		
		// skip formulas until we're at the starting formula
		if (startingFormula != null) {
			Formula f;
			while ((f= next()).getFormulaText().equals(startingFormula.getFormulaText()) == false) {
				System.out.println("Skipping formula:"+f);
			}
		}
	}

	public boolean hasNext() {
		while (true) {
			if (_negationConstructor != null) {
				if (_negationConstructor.hasNext())
					return true;
				_negationConstructor.close();
				_negationConstructor= null;
				if (_rightLength < 1)
					return false;
				_ifthenConstructor= new IfThenFormulaConstructor(_rightLength);
			}
			else {
				if (_ifthenConstructor == null)
					return false;
				if (_ifthenConstructor.hasNext())
					return true;
				_ifthenConstructor.close();
				_ifthenConstructor= null;
				if (0 < --_rightLength) 
					_ifthenConstructor= new IfThenFormulaConstructor(_rightLength);
			}
		}
	}

	public Formula next() {
		if (!hasNext())
			throw new NoSuchElementException();
		if (_negationConstructor != null)
			return _negationConstructor.next();
		return _ifthenConstructor.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		if (_negationConstructor != null) {
			_negationConstructor.close();
			_negationConstructor= null;
		}
		if (_ifthenConstructor != null) {
			_ifthenConstructor.close();
			_negationConstructor= null;
		}
	}
	
	class NegationFormulaConstructor implements ResultIterator<Formula> {

		ResultIterator<Formula> _formulas;
		NegationFormulaConstructor() {
			_formulas= _database.findCanonicalFormulasByLength(_formulaLength - 1);
		}

		public boolean hasNext() {
			return _formulas.hasNext();
		}

		public Formula next() {
			if (!_formulas.hasNext())
				throw new NoSuchElementException();
			return _system.createNegation(_formulas.next());
		}
		
		@Override
		public void close() {
			if (_formulas != null)
				_formulas.close();
			_formulas= null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	class IfThenFormulaConstructor implements ResultIterator<Formula> {

		ResultIterator<Formula> _rightIterator;
		ResultIterator<Formula> _consequents;
		Formula _antecedent= null;
		IfThenFormulaConstructor(int lengthOfRightSideFormulas) {
			_rightIterator= _database.findCanonicalFormulasByLength(lengthOfRightSideFormulas);
			_consequents= _database.findCanonicalFormulasByLength(_formulaLength - lengthOfRightSideFormulas - 1);
			if (!_rightIterator.hasNext() || !_consequents.hasNext()) {
				close();
			}
			else
				_antecedent= _rightIterator.next();
		}
		
		@Override
		public void close() {
			if (_rightIterator != null)
				_rightIterator.close();
			if (_consequents != null)
				_consequents.close();
			_rightIterator= null;
			_consequents= null;
			_antecedent= null;
		}

		public boolean hasNext() {
			if (_antecedent == null)
				return false;
			if (_consequents.hasNext())
				return true;
			if (!_rightIterator.hasNext()) {
				close();
				return false;
			}
			_antecedent= _rightIterator.next();
			_consequents.close();
			_consequents= _database.findCanonicalFormulasByLength(_formulaLength - _antecedent.length() - 1);
			return true;
		}

		public Formula next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return _system.createImplication(_antecedent, _consequents.next());
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}

