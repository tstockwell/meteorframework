package sat;

import java.util.NoSuchElementException;

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
		
		// skip formulas until we're at the starting formula
		if (startingFormula != null) {
			Formula f;
			while ((f= next()).toString().equals(startingFormula.toString()) == false) {
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
			return new Formula(Formula.NEGATION, _formulas.next());
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
		ResultIterator<Formula> _leftIterator;
		Formula _rightFormula= null;
		IfThenFormulaConstructor(int lengthOfRightSideFormulas) {
			_rightIterator= _database.findCanonicalFormulasByLength(lengthOfRightSideFormulas);
			_leftIterator= _database.findCanonicalFormulasByLength(_formulaLength - lengthOfRightSideFormulas - 1);
			if (!_rightIterator.hasNext() || !_leftIterator.hasNext()) {
				close();
			}
			else
				_rightFormula= _rightIterator.next();
		}
		
		@Override
		public void close() {
			if (_rightIterator != null)
				_rightIterator.close();
			if (_leftIterator != null)
				_leftIterator.close();
			_rightIterator= null;
			_leftIterator= null;
			_rightFormula= null;
		}

		public boolean hasNext() {
			if (_rightFormula == null)
				return false;
			if (_leftIterator.hasNext())
				return true;
			if (!_rightIterator.hasNext()) {
				close();
				return false;
			}
			_rightFormula= _rightIterator.next();
			_leftIterator.close();
			_leftIterator= _database.findCanonicalFormulasByLength(_formulaLength - _rightFormula.length() - 1);
			return true;
		}

		public Formula next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return new Formula(Formula.IF_THEN, _leftIterator.next());
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}

