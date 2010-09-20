package sat;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import sat.TruthTable.Builder;


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
public class PropostionalSystem {
	
	/*
	 * For efficiency we cache formulas and truth tables.
	 */
	final private Map<String, FormulaReference> __formulaCache= new TreeMap<String, FormulaReference>(); 
	final private Map<String, SoftReference<TruthTable>> __ttCache= new TreeMap<String, SoftReference<TruthTable>>(); 
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
	
	final int _maxTruthValues;
	final int _maxTruthTables;

	/**
	 * @param variableCount  the number of variables in the system.
	 * 		Variables are numbered from 1 to variableCount
	 */
	public PropostionalSystem(int variableCount) {
		assert 0 < variableCount;
		_variableCount= variableCount;
		_variableLength= Integer.toString(variableCount).length();
		_maxTruthValues= 1 << _variableCount;
		_maxTruthTables= 1 << _maxTruthValues;
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
				__ttCache.remove(ref._string);
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

	public long getMaxTruthValues() {
		return _maxTruthValues;
	}

	public long getMaxTruthTables() {
		return _maxTruthTables;
	}
	
	
	public TruthTable getTruthTable(Formula formula) {
		SoftReference<TruthTable> ref= __ttCache.get(formula.getFormulaText());
		if (ref == null || ref.get() == null) {
			_truthTable= TruthTable.create(new TruthTable.Builder() {
				@Override
				public boolean evaluate(Map<String, Boolean> values) {
					return Formula.this.evaluate(values);
				}
			});
		}
		return ref.get();
	}

	

	
	private String __zeropad; 
	{
		char[] pad= new char[_maxTruthValues];
		for (int i= pad.length; 0 < i--;)
			pad[i]= '0';
		__zeropad= new String(pad);
	}
	
	private final String[] _truthTableStrings= new String[_maxTruthTables];
	{
		for (int i= 0; i < _maxTruthTables; i++) {
			String s= Integer.toString(i, 2);
			int l= _maxTruthValues - s.length();
			if (0 < l) 
				s= __zeropad.substring(0, l) + s;
			_truthTableStrings[i]= s;
		}
	}
	
	private final Map<String, TruthTable> __tables= new TreeMap<String, TruthTable>();
	{
		for (int i= 0; i < _maxTruthTables; i++) 
			__tables.put(_truthTableStrings[i], new TruthTable(i));
	}
	
	public class TruthTable {
		
		public static interface Builder {
			public boolean evaluate(Map<String, Boolean> values);
		}
		
		private int _truthTable; 
		
		private TruthTable(int truthTable) {
			_truthTable= truthTable;
		}
		public static TruthTable create(Builder builder) {
			char[] truthValues= new char[MAX_TRUTH_VALUES];
			for (int i= 0; i < MAX_TRUTH_VALUES; i++) {
				String v= Integer.toString(i, 2);
				int l= Formula.MAX_VARIABLES - v.length();
				if (0 < l) 
					v= __zeropad.substring(0, l) + v;
				truthValues[i]= builder.evaluate(v) ? '1' : '0';
			}
			return __tables.get(new String(truthValues));
		}
		public static TruthTable create(String booleanString) {
			return __tables.get(booleanString);
		}
		public static TruthTable create(int i) {
			return __tables.get(_truthTableStrings[i]);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TruthTable) 
				return ((TruthTable)obj)._truthTable == _truthTable;
			return false;			
		}
		
		@Override
		public int hashCode() {
			return _truthTable;
		}
		
		public String toString() {
			return _truthTableStrings[_truthTable];
		}
		
	}
	
}
