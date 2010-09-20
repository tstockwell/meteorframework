package sat;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;


/**
 * A propositional system manages the creation of propositional formulas and 
 * provides facilities for testing the validity of formulas.
 * This system represents propositional formulas using only...
 * 	...TRUE and FALSE.
 *  ...variables.
 *  ...a negation operator 
 *  ...an implication operator (aka the if-then operator, most often written as ->).
 *  
 * This system supports a textual form for representing formulas.
 * The normal form uses Polish notation and...
 * 	...the symbols 'T' and 'F' for TRUE and FALSE.
 *  ...the symbol '~' for the negation operator. 
 *  ...the symbol '*' for the implication operator. 
 *  ...numeric symbols for representing variables.
 *  	Variable symbols must be prepended with zeros so that all numbers are the same 
 *  	length as the largest variable in the system.
 *  
 *  Use the static PropositionalSystem.create* methods to create instances of formulas.
 *  
 * @author 	Ted Stockwell <emorning@yahoo.com>
 */
public class PropositionalSystem {
	
	/*
	 * For efficiency we cache formulas and truth tables.
	 */
	final private Map<String, FormulaReference> __formulaCache= new TreeMap<String, FormulaReference>(); 
	final private ReferenceQueue<Formula> __referenceQueue= new ReferenceQueue<Formula>();
	private class FormulaReference extends SoftReference<Formula> {
		String _string;
		public FormulaReference(Formula referent) {
			super(referent, __referenceQueue);
			_string= referent.getFormulaText();
		}
	}
	
	
	final int _variableCount;
	final int _variableLength;
	
	/**
	 * @param variableCount  the number of variables in the system.
	 * 		Variables are numbered from 1 to variableCount
	 */
	public PropositionalSystem(int variableCount) {
		assert 0 < variableCount;
		_variableCount= variableCount;
		_variableLength= Integer.toString(variableCount).length();
		__formulaCache.put(Constant.TRUE.getFormulaText(), new FormulaReference(Constant.TRUE));
		__formulaCache.put(Constant.FALSE.getFormulaText(), new FormulaReference(Constant.FALSE));
	}
	
	public int getVariableCount() {
		return _variableCount;
	}

	/**
	 * Create a formula from its textual representation in normal form
	 */
	public Formula createFormula(String path) {
		Formula formula= null;
		synchronized (__formulaCache) {
			Reference<Formula> ref= __formulaCache.get(path);
			if (ref == null || (formula= ref.get()) == null) {
				formula= parse(path);
				__formulaCache.put(path, new FormulaReference(formula));
			}
		}
		
		// clean up expired references
		FormulaReference ref;
		while ((ref= (FormulaReference)__referenceQueue.poll()) != null) {
			synchronized (__formulaCache) {
				__formulaCache.remove(ref._string);
			}
		}
		
		return formula;
	}

	public Formula createNegation(Formula right) {
		return createFormula(Operator.Negation+right.getFormulaText());
	}

	public Formula createImplication(Formula antecedent, Formula consequent) {
		return createFormula(Operator.Implication+antecedent.getFormulaText()+consequent.getFormulaText());
	}
	public Formula createVariable(int variable) {
		if (variable < 1)
			throw new RuntimeException("Variable numbers must be greater than 0");
		if (_variableCount < variable)
			throw new RuntimeException("Variable numbers must be less than or equal to the number of variables in the system");
		String text= Integer.toString(variable);
		while (text.length() < _variableLength)
			text= "0"+text;
		return createFormula(text);
	}

	private Formula parse(String formula) {
		Stack<Formula> stack= new Stack<Formula>();
		for (int i = formula.length(); 0 < i--;) {
			char c = formula.charAt(i);
			if (Operator.isNegation(c)) {
				Formula f= stack.pop();
				String text= formula.substring(i, i + f.length()+1);
				stack.push(new Negation(f, text));
			}
			else if (Operator.isImplication(c)) {
				Formula antecendent= stack.pop();
				Formula consequent= stack.pop();
				String text= formula.substring(i, i + antecendent.length()+consequent.length()+1);
				stack.push(new Implication(antecendent, consequent, text));
			}
			else if (c == 'T') {
				stack.push(Constant.TRUE);
			}
			else if (c == 'F') {
				stack.push(Constant.FALSE);
			}
			else if (Character.isDigit(c)) {
				int end= i+1;
				while (0 < i && Character.isDigit(formula.charAt(i-1))) 
					i--;
				String text= formula.substring(i, end);
				while (text.length() < _variableLength)
					text= "0"+text;
				stack.push(new Variable(text));
			}
			else 
				throw new RuntimeException("Unknown symbol:"+c);
		}
		
		if (stack.size() != 1) 
			throw new RuntimeException("Invalid postcondition after evaluating formula wellformedness: count < 1");
		
		Formula formula2= stack.pop();
		return formula2;
	}
	
	public int getVariableLength() {
		return _variableLength;
	}


}
