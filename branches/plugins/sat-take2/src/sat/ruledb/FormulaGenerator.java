package sat.ruledb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sat.Constant;
import sat.Formula;
import sat.PropositionalSystem;

/**
 * Enumerates all formulas that need to be processed by the RuleGenerator application.
 * Creates new formulas by assembling formulas from previously 
 * created canonical formulas in the formula database.
 * 
 * @author Ted Stockwell
 */
public class FormulaGenerator {
	
	final RuleDatabase _database;
	final PropositionalSystem _system;
	int _startingLength= 0;
	int _currentLength= 0;
	ResultIterator<Formula> _currentIterator;
	
	List<Formula> _startingFormulas= new ArrayList<Formula>();
	

	public FormulaGenerator(RuleDatabase database) {
		_database= database;
		_system= database.getSystem();
		
		_startingFormulas.add(Constant.FALSE);
		_startingFormulas.add(Constant.TRUE);
		for (int i= 1; i <= RuleDatabase.VARIABLE_COUNT; i++)
			_startingFormulas.add(_system.createVariable(i));
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
			
			_currentIterator.close();
			
			FormulaConstructor nextConstructor= null;
			while (nextConstructor == null) {
				_currentLength++;
				System.out.println("The formulas lengths have been increased to "+_currentLength);
				
				if (TruthTables.MAX_TRUTH_TABLES <= _database.countCanonicalTruthTables())
					if (_database.lengthOfLongestPossibleNonReducableFormula() < _currentLength) {
						System.out.println("!!!!!! The Rule Database is Complete !!!");
						return null;
					} 
				
				FormulaConstructor fc= new FormulaConstructor(_database, _currentLength);
				if (fc.hasNext())
					nextConstructor= fc;
			}
			_currentIterator= nextConstructor;
			
		}
		return _currentIterator.next(); 
	}

}
