package sat;

public class ReductionRule {
	public Formula formula;
	public Formula reduction;
	
	public ReductionRule(Formula formula, Formula reduction) {
		this.formula= formula;
		this.reduction= reduction;
	}
	
	@Override
	public String toString() {
		return formula.getFormulaText()+ " ==> "+reduction.getFormulaText();
	}
}
