package sat;

import java.util.NoSuchElementException;

/**
 * Creates new formulas of a specified length by assembling 
 * the new formulas using previously created formulas in the 
 * database. 
 */
public class FormulaConstructor implements ResultIterator<Formula> {

	RuleDatabase _database;
	int _formulaLength;
	int _leftSize= 0;
	int _rightSize= 0;
	int _state= 0; // 0 == generating formulas that being with ~, 1 == generating formulas that begin with *. >1 no more formulas.
	ResultIterator<Formula> _leftIterator;
	ResultIterator<Formula> _rightIterator;
	Formula _leftFormula= null;
	Formula _rightFormula= null;
	int _hasNext= -1;
	
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
		_rightSize= _formulaLength - 1;
		_rightIterator= _database.findCanonicalFormulasByLength(_rightSize);
		
		// skip formulas until we're at the starting formula
		if (startingFormula != null) {
			Formula f;
			while ((f= next()).toString().equals(startingFormula.toString()) == false) {
				System.out.println("Skipping formula:"+f);
			}
		}
	}

	public boolean hasNext() {
		if (_hasNext < 0) {
			if (_state == 0) {
				if (_rightIterator.hasNext()) { 
					_hasNext= 1;
				}
				else {
					_state= 1;
					_rightIterator.close();
					_rightSize= _formulaLength - 2;
					_leftSize= 1;
					if (0 < _rightSize) {
						_rightIterator= _database.findCanonicalFormulasByLength(_rightSize);
						_leftIterator= _database.findCanonicalFormulasByLength(_leftSize);
						if (_leftIterator.hasNext()) {
							_leftFormula= _leftIterator.next();
							_hasNext= 1;
						}
						else 
							_hasNext= 0;
					}
					else 
						_hasNext= 0;
				}
			}
			else if (_state == 1) {
				if (_rightIterator.hasNext()) {
					_hasNext= 1;
				}
				else if (_leftIterator.hasNext()) {
					_leftFormula= _leftIterator.next();
					_rightIterator.close();
					_rightIterator= _database.findCanonicalFormulasByLength(_rightSize);
					_hasNext= 1;
				}
				else if (_leftSize < _formulaLength - 2) {
					_rightIterator.close();
					_leftIterator.close();
					_leftSize++;
					_rightSize--;
					_rightIterator= _database.findCanonicalFormulasByLength(_rightSize);
					_leftIterator= _database.findCanonicalFormulasByLength(_leftSize);
					if (_leftIterator.hasNext()) {
						_leftFormula= _leftIterator.next();
						_hasNext= 1;
					}
					else
						_hasNext= 0;
				}
				else 
					_hasNext= 0;
			}
			else if (_state == 2)
				_hasNext= 0;
		}
		if (_hasNext == 0)
			_state= 2;
		return (_hasNext == 0) ? false : true;
	}

	public Formula next() {
		
		if (_hasNext < 0)
			if (!hasNext())
				throw new NoSuchElementException();
		_hasNext= -1;
		
		if (_state == 0) 
			return new Formula(Formula.NEGATION, _rightIterator.next());
		if (_state == 1) 
			return new Formula(Formula.IF_THEN, _leftFormula, _rightIterator.next());
		throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void close() {
		if (_rightIterator != null)
			_rightIterator.close();
		if (_leftIterator != null)
			_leftIterator.close();
		
	}
	
}
