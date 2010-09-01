package sat;

import java.math.BigInteger;



public class TruthTable {
	
	public static final int MAX_TRUTH_VALUES= 1 << Formula.MAX_VARIABLES;
	
	private static String __zeropad; 
	static {
		char[] pad= new char[MAX_TRUTH_VALUES];
		for (int i= pad.length; 0 < i--;)
			pad[i]= '0';
		__zeropad= new String(pad);
	}
	
	public static interface Builder {
		boolean evaluate(String booleanString);
	}
	
	String _booleanString="";
	
	public TruthTable(String booleanString) {
		_booleanString= booleanString;
	}
	public TruthTable(Builder builder) {
		char[] truthValues= new char[MAX_TRUTH_VALUES];
		for (int i= 0; i < MAX_TRUTH_VALUES; i++) {
			String v= Integer.toString(i, 2);
			if (v.length() < MAX_TRUTH_VALUES) 
				v= __zeropad.substring(0, MAX_TRUTH_VALUES - v.length()) + v;
			truthValues[i]= builder.evaluate(v) ? '1' : '0';
		}
		_booleanString= new String(truthValues);
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
