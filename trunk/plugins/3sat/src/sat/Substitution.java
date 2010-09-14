package sat;

public class Substitution {
	
	public static final Substitution EMPTY= new Substitution();
	
	private Formula[] _formulas= new Formula[Formula.MAX_VARIABLES];
	
	private Substitution() {
	}
	public Substitution(char variable, Formula formula) {
		_formulas[variable-'a']= formula;
	}
	
	public Substitution union(Substitution t) {
		Substitution s= new Substitution();
		for (int i= 0; i < Formula.MAX_VARIABLES; i++) {
			Formula f;
			if ((f= _formulas[i]) != null) {
				if (t._formulas[i] != null) 
					throw new RuntimeException("two substitutions for same variable");
				s._formulas[i]= f;
			}
			else
				s._formulas[i]= t._formulas[i];
		}
		return s;
	}
	
	public Formula getFormula(char variable) {
		return _formulas[variable-'a'];
	}
	
}
