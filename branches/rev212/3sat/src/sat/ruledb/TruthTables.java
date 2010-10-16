package sat.ruledb;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.TreeMap;

import sat.Formula;
import sat.PropositionalSystem;


/**
 * A specialized container for efficiently managing truth tables 
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
public class TruthTables {
	public static final int MAX_TRUTH_VALUES= 1 << RuleDatabase.VARIABLE_COUNT;
	public static final int MAX_TRUTH_TABLES= 1 << MAX_TRUTH_VALUES;

	private static String __zeropad; 
	static {
		char[] pad= new char[MAX_TRUTH_VALUES];
		for (int i= pad.length; 0 < i--;)
			pad[i]= '0';
		__zeropad= new String(pad);
	}
	
	private final Map<String, Boolean>[] __valuations= new Map[MAX_TRUTH_VALUES];
	private void init_valuations() {
		for (int i= 0; i < MAX_TRUTH_VALUES; i++) {
			String v= Integer.toString(i, 2);
			int l= RuleDatabase.VARIABLE_COUNT - v.length();
			if (0 < l) 
				v= __zeropad.substring(0, l) + v;
			
			Map<String, Boolean> valuation= new TreeMap<String, Boolean>();
			for (int j= 1; j <= RuleDatabase.VARIABLE_COUNT; j++) {
				valuation.put(
						_system.createVariable(j).getSymbol(), 
						v.charAt(v.length()-j) == '0' ? Boolean.FALSE : Boolean.TRUE);
			}
			__valuations[i]= valuation;
		}
	}

	
	/*
	 * All truth tables have an associated human-readable string.
	 * Here we construct the human-readable strings for the tables
	 * String are read from left to right, the leftmost character is the 
	 * value when all variables are true, the rightmost variable is 
	 * the value of the formula when all variables are false.    
	 */
	private final String[] _truthTableStrings= new String[MAX_TRUTH_TABLES];
	private void init_truth_tables() {
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
	private final Map<String, TruthTable> __tables= new TreeMap<String, TruthTable>();
	private void init_tables() {
		for (int i= 0; i < MAX_TRUTH_TABLES; i++) {
			String key= _truthTableStrings[i];
			__tables.put(key, new TruthTable(i, key));
		}
	}

	// cache truth tables for formulas 
	final private ReferenceQueue<Formula> __referenceQueue= new ReferenceQueue<Formula>();
	private class FormulaReference extends SoftReference<Formula> {
		String _string;
		public FormulaReference(Formula referent) {
			super(referent, __referenceQueue);
			_string= referent.getFormulaText();
		}
	}
	final private Map<String, TruthTable> __tablesForFormulas= new TreeMap<String, TruthTable>(); 
	final private Map<String, FormulaReference> __formulas= new TreeMap<String, FormulaReference>(); 
	
	
	public static interface Builder {
		public boolean evaluate(Map<String, Boolean> valuation);
	}
	
	public TruthTable create(Builder builder) {
		char[] truthValues= new char[MAX_TRUTH_VALUES];
		for (int i= MAX_TRUTH_VALUES; 0 < i--;) 
			truthValues[MAX_TRUTH_VALUES-i-1]= builder.evaluate(__valuations[i]) ? '1' : '0';
		return __tables.get(new String(truthValues));
	}
	public TruthTable create(String booleanString) {
		return __tables.get(booleanString);
	}
	public TruthTable create(int i) {
		return __tables.get(_truthTableStrings[i]);
	}
	
	public TruthTable getTruthTable(final Formula formula) {
		TruthTable truthTable= null;
		String path= formula.getFormulaText();
		synchronized (__tablesForFormulas) {
			truthTable= __tablesForFormulas.get(path);
			if (truthTable == null) {
				__formulas.put(path, new FormulaReference(formula));
				truthTable= create(new TruthTables.Builder() {
					public boolean evaluate(Map<String, Boolean> valuation) {
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
	
	final private PropositionalSystem _system;
	public TruthTables(PropositionalSystem system) {
		_system= system;
		init_valuations();
		init_truth_tables();
		init_tables();
	}
	
	public PropositionalSystem getSystem() {
		return _system;
	}
	
}
