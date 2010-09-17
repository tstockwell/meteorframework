package sat.ruledb;

import java.util.List;

import sat.Formula;

/**
 * Spits out a bunch of information about the formulas in the Rule Database.
 *  
 * @author Ted Stockwell
 *
 */
public class DatabaseReport {
	
	private static final Object SHOW_REDUCTION_RULES = "-showReductionRules";

	public static void main(String[] args)
	throws Exception
	{
		boolean showReductionRules= false;
		
		for (String arg:args)
			if (arg.equals(SHOW_REDUCTION_RULES))
				showReductionRules= true;
		
		final RuleDatabase database= new RuleDatabase();
		
		/* 
		 * Count # of rules
		 */
		System.out.println("Total number of canonical formulas is "+database.countCanonicalFormulas());
		System.out.println("Total number of non-canonical formulas  is "+database.countNonCanonicalFormulas());
		
		int tt= 0;
		for (int truthValue= 0; truthValue < TruthTable.MAX_TRUTH_TABLES; truthValue++) {
			TruthTable truthTable= TruthTable.create(truthValue);
			if (0 < database.getLengthOfCanonicalFormulas(truthTable))
				tt++;
		}
		System.out.println("Have found forumlas for "+tt+" of "+TruthTable.MAX_TRUTH_TABLES+ " truth tables");
		System.out.println("The length of the longest canonical formula is "+database.getLengthOfLongestCanonicalFormula());
		System.out.println();
		
		
		

		/*
		 * List lengths and # of canonical formulas
		 */
		database.getLengthOfCanonicalFormulas(TruthTable.create(0));
		System.out.println("TRUTH VALUE     LENGTH    COUNT");
		System.out.println("                FORMULAS");
		System.out.println("-------------   ------   ------");
		for (int truthValue= 0; truthValue < TruthTable.MAX_TRUTH_TABLES; truthValue++) {
			TruthTable truthTable= TruthTable.create(truthValue);
			List<Formula> canonicalFormulas= database.getCanonicalFormulas(truthTable);
			
			String t= ""+truthValue+"("+truthTable+")                 ";
			t= t.substring(0, 13);

			if (canonicalFormulas.isEmpty()) {
				System.out.println(t+"   *not yet determined*");
			}
			else {
				String l= "      "+canonicalFormulas.get(0).length();
				l= l.substring(l.length()-6);
				String c= "      "+canonicalFormulas.size();
				c= c.substring(c.length()-6);
				System.out.println(t+"   "+l+"   "+c);
				for (Formula formula:canonicalFormulas) {
					System.out.println("              "+formula.getFormulaText());
				}
			}
			System.out.println("-------------   ------   ------");
		}
		
		if (showReductionRules) {
			System.out.println();
			System.out.println("Reduction Rules");
			System.out.println("=====================================");
			ResultIterator<Formula> nonCanonicalFormulas= database.getAllNonCanonicalFormulas();
			long count= 0;
			while (nonCanonicalFormulas.hasNext()) {
				Formula formula= nonCanonicalFormulas.next();
				System.out.println(new ReductionRule(formula, database.findCanonicalFormula(formula)));
				count++;
			}
		}
		
		
	}
	
}
