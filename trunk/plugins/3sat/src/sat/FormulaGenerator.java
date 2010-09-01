package sat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Enumerates all formulas that need to be processed by the RuleGenerator application.
 * Creates new formulas by assembling the new formulas using previously 
 * created formulas in the formula database.
 * 
 * @author Ted Stockwell
 */
public class FormulaGenerator {
	
	RuleDatabase _database;
	int _startingLength= 0;
	int _currentLength= 0;
	ResultIterator<Formula> _currentIterator;
	
	List<Formula> _startingFormulas= new ArrayList<Formula>();
	{
		_startingFormulas.add(Formula.FALSE);
		_startingFormulas.add(Formula.TRUE);
		for (int i= 0; i < Formula.VARIABLES.length; i++)
			_startingFormulas.add(Formula.VARIABLES[i]);
	}
	

	public FormulaGenerator(RuleDatabase database) {
		_database= database;
	}

	public Formula getStartingFormula() {
		Formula formula = _database.getLastGeneratedFormula();
		if (formula == null) {
			_currentLength= 1;
			_currentIterator= new ResultIterator<Formula>() {
				Iterator<Formula> _iterator= _startingFormulas.iterator();
				public void close() {
					// do nothing
				}
				public boolean hasNext() {
					return _iterator.hasNext();
				}
				public Formula next() {
					return _iterator.next();
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} 
		else {
			_currentLength= formula.length();
			_currentIterator= new FormulaConstructor(_database, formula); 
		}
		return _currentIterator.next();
	}

	public Formula getNextWellFormedFormula() {
		if (_currentIterator.hasNext() == false) {
			
			FormulaConstructor nextConstructor= null;
			while (nextConstructor == null) {
				_currentLength++;
				System.out.println("The formulas lengths have been increased to "+_currentLength);
				FormulaConstructor fc= new FormulaConstructor(_database, _currentLength);
				if (fc.hasNext())
					nextConstructor= fc;
			}
			_currentIterator= nextConstructor;
			
		}
		return _currentIterator.next(); 
	}

}
