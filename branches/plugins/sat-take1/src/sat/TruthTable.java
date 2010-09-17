package sat;

import java.util.Map;
import java.util.TreeMap;



public class TruthTable {
	
	public static final int MAX_TRUTH_VALUES= 1 << Formula.MAX_VARIABLES;
	public static final int MAX_TRUTH_TABLES= 1 << MAX_TRUTH_VALUES;
	
	private static String __zeropad; 
	static {
		char[] pad= new char[MAX_TRUTH_VALUES];
		for (int i= pad.length; 0 < i--;)
			pad[i]= '0';
		__zeropad= new String(pad);
	}
	
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
	
	private static final Map<String, TruthTable> __tables= new TreeMap<String, TruthTable>();
	static {
		for (int i= 0; i < MAX_TRUTH_TABLES; i++) 
			__tables.put(_truthTableStrings[i], new TruthTable(i));
	}
	
	public static interface Builder {
		boolean evaluate(String booleanString);
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
