package sat.ruledb;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import sat.Formula;
import sat.Variable;



@SuppressWarnings("unchecked")
public class TruthTable {
	public static final int MAX_TRUTH_VALUES= 1 << RuleDatabase.VARIABLE_COUNT;
	public static final int MAX_TRUTH_TABLES= 1 << MAX_TRUTH_VALUES;

	private static String __zeropad; 
	static {
		char[] pad= new char[MAX_TRUTH_VALUES];
		for (int i= pad.length; 0 < i--;)
			pad[i]= '0';
		__zeropad= new String(pad);
	}
	
	private static final Map<Variable, Boolean>[] __valuations= new Map[MAX_TRUTH_VALUES];
	static {
		for (int i= 0; i < MAX_TRUTH_VALUES; i++) {
			String v= Integer.toString(i, 2);
			int l= RuleDatabase.VARIABLE_COUNT - v.length();
			if (0 < l) 
				v= __zeropad.substring(0, l) + v;
			
			Map<Variable, Boolean> valuation= new HashMap<Variable, Boolean>();
			for (int j= 1; j <= RuleDatabase.VARIABLE_COUNT; j++) {
				valuation.put(
						RuleGenerator.SYSTEM.createVariable(j), 
						v.charAt(v.length()-j) == '0' ? Boolean.FALSE : Boolean.TRUE);
			}
			__valuations[i]= valuation;
		}
	}

	
	/*
	 * All truth tables have an associated human-readable string.
	 * Here we construct the human-readable strings for the tables   
	 */
	private static final String[] _truthTableStrings= new String[MAX_TRUTH_TABLES];
	static {
		for (int i= 0; i < MAX_TRUTH_TABLES; i++) {
			String s= Integer.toString(i, 2);
			int l= MAX_TRUTH_VALUES - s.length();
			if (0 < l) 
				s= __zeropad.substring(0, l) + s;
			_truthTableStrings[i]= s;
		}
	}
	
	/**
	 * For 3 variables there are only 255 truth tables.
	 * Here we create all possible truth tables and store them in a map 
	 * indexed by their associated string representation.
	 */
	private static final Map<String, TruthTable> __tables= new TreeMap<String, TruthTable>();
	static {
		for (int i= 0; i < MAX_TRUTH_TABLES; i++) 
			__tables.put(_truthTableStrings[i], new TruthTable(i));
	}

	// cache truth tables for formulas 
	static final private ReferenceQueue<Formula> __referenceQueue= new ReferenceQueue<Formula>();
	private static class FormulaReference extends SoftReference<Formula> {
		String _string;
		public FormulaReference(Formula referent) {
			super(referent, __referenceQueue);
			_string= referent.getFormulaText();
		}
	}
	static final private Map<String, TruthTable> __tablesForFormulas= new TreeMap<String, TruthTable>(); 
	static final private Map<String, FormulaReference> __formulas= new TreeMap<String, FormulaReference>(); 
	
	
	public static interface Builder {
		public boolean evaluate(Map<Variable, Boolean> valuation);
	}
	
	private int _truthTable; 
	
	private TruthTable(int truthTable) {
		_truthTable= truthTable;
	}
	public static TruthTable create(Builder builder) {
		char[] truthValues= new char[MAX_TRUTH_VALUES];
		for (int i= 0; i < MAX_TRUTH_VALUES; i++) 
			truthValues[i]= builder.evaluate(__valuations[i]) ? '1' : '0';
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
	public static TruthTable getTruthTable(final Formula formula) {
		TruthTable truthTable= null;
		String path= formula.getFormulaText();
		synchronized (__tablesForFormulas) {
			truthTable= __tablesForFormulas.get(path);
			if (truthTable == null) {
				__formulas.put(path, new FormulaReference(formula));
				truthTable= create(new TruthTable.Builder() {
					public boolean evaluate(Map<Variable, Boolean> valuation) {
						return formula.evaluate(valuation);
					}
				});
				__tablesForFormulas.put(path, truthTable);
			}
		}
		
		// clean up expired references
		FormulaReference ref;
		while ((ref= (FormulaReference)__referenceQueue.poll()) != null) {
			synchronized (__formulas) {
				__formulas.remove(ref._string);
				__tablesForFormulas.remove(ref._string);
			}
		}
		
		return truthTable;
	}
	
}
