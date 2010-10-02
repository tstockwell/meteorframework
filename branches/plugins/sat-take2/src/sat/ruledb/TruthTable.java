package sat.ruledb;




public class TruthTable {

	private int _truthTable; 
	private String _key; 
	
	TruthTable(int truthTable, String key) {
		_truthTable= truthTable;
		_key= key;
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
		return _key;
	}
}
