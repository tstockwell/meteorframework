package sat;

import java.util.HashMap;



public class TruthTable {
	
	public static final int MAX_TRUTH_VALUES= 1 << Formula.MAX_VARIABLES;
	public static final long MAX_TRUTH_TABLES= (long)Math.pow(2, MAX_TRUTH_VALUES);
	
	private static String __zeropad; 
	static {
		char[] pad= new char[MAX_TRUTH_VALUES];
		for (int i= pad.length; 0 < i--;)
			pad[i]= '0';
		__zeropad= new String(pad);
	}
	
	private static final HashMap<String, TruthTable> __tables= new HashMap<String, TruthTable>();
	static {
		for (int i= 0; i < MAX_TRUTH_TABLES; i++) {
			String s= toTruthTableString(i);
			__tables.put(s, new TruthTable(toTruthTableString(i)));
		}
	}
	public static final String toTruthTableString(int i) {
			String s= Integer.toString(i, 2);
			int l= MAX_TRUTH_VALUES - s.length();
			if (0 < l) 
				s= __zeropad.substring(0, l) + s;
			return s;
	}
	
	public static interface Builder {
		boolean evaluate(String booleanString);
	}
	
	String _booleanString="";
	
	private TruthTable(String booleanString) {
		_booleanString= booleanString;
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
		return __tables.get(toTruthTableString(i));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TruthTable) 
			return ((TruthTable)obj)._booleanString.equals(_booleanString);
		return false;			
	}
	
	@Override
	public int hashCode() {
		return _booleanString.hashCode();
	}
	
	public String toString() {
		return _booleanString;
	}
	
}
