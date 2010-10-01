package sat;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;


/**
 * A propositional system manages the creation of propositional formulas and 
 * provides facilities for testing the validity of formulas.
 * This system represents propositional formulas using only...
 * 	...TRUE and KEY_FALSE.
 *  ...variables.
 *  ...a negation operator 
 *  ...an implication operator (aka the if-then operator, most often written as ->).
 *  
 * This system supports a textual form for representing formulas.
 * The normal form uses Polish notation and...
 * 	...the symbols 'T' and 'F' for KEY_TRUE and KEY_FALSE.
 *  ...the symbol '-' for the negation operator. 
 *  ...the symbol '*' for the implication operator. 
 *  ...the symbol '^' followed by a number for representing variables.
 *  
 *  Use the static PropositionalSystem.create* methods to create instances of formulas.
 *  
 * @author 	Ted Stockwell <emorning@yahoo.com>
 */
public class PropositionalSystem {
	
	/*
	 * For efficiency we cache formulas and truth tables.
	 */
	final private Map<String, FormulaReference> __formulaCache= new TreeMap<String, FormulaReference>(); 
	final private ReferenceQueue<Formula> __referenceQueue= new ReferenceQueue<Formula>();
	private class FormulaReference extends SoftReference<Formula> {
		String _string;
		public FormulaReference(Formula referent) {
			super(referent, __referenceQueue);
			_string= referent.getFormulaText();
		}
	}
	
	
	/**
	 * @param variableCount  the number of variables in the system.
	 * 		Variables are numbered from 1 to variableCount
	 */
	public PropositionalSystem() {
		__formulaCache.put(Constant.TRUE.getFormulaText(), new FormulaReference(Constant.TRUE));
		__formulaCache.put(Constant.FALSE.getFormulaText(), new FormulaReference(Constant.FALSE));
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
				addFormula(formula);
			}
		}
		
		return formula;
	}
	private void addFormula(Formula formula) {
		synchronized (__formulaCache) {
			__formulaCache.put(formula.getFormulaText(), new FormulaReference(formula));

			// clean up expired references
			FormulaReference ref;
			while ((ref= (FormulaReference)__referenceQueue.poll()) != null) {
				synchronized (__formulaCache) {
					__formulaCache.remove(ref._string);
				}
			}

		}
	}

	public Negation createNegation(Formula right) {
		return createNegation(right, Operator.Negation.getFormulaText()+right.getFormulaText());
	}
	private Negation createNegation(Formula right, String text) {
		Negation formula= null;
		synchronized (__formulaCache) {
			FormulaReference ref= __formulaCache.get(text);
			if (ref == null || (formula= (Negation)ref.get()) == null) {
				formula= new Negation(right, text);
				addFormula(formula);
			}
		}
		
		return formula;
	}

	public Implication createImplication(Formula antecedent, Formula consequent) {
		return createImplication(antecedent, consequent, Operator.Implication.getFormulaText()+antecedent.getFormulaText()+consequent.getFormulaText());
	}
	private Implication createImplication(Formula antecedent, Formula consequent, String text) {
		Implication formula= null;
		synchronized (__formulaCache) {
			FormulaReference ref= __formulaCache.get(text);
			if (ref == null || (formula= (Implication)ref.get()) == null) {
				formula= new Implication(antecedent, consequent, text);
				addFormula(formula);
			}
		}
		
		return formula;
	}
	public Variable createVariable(int variable) {
		if (variable < 1)
			throw new RuntimeException("Variable numbers must be greater than 0");
		return createVariable("^"+Integer.toString(variable));
	}
	private Variable createVariable(String text) {
		Variable formula= null;
		synchronized (__formulaCache) {
			FormulaReference ref= __formulaCache.get(text);
			if (ref == null || (formula= (Variable)ref.get()) == null) {
				formula= new Variable(text);
				addFormula(formula);
			}
		}
		return formula;
	}
	
	/**
	 * This method finds the end of a formula.
	 * @param formulaText A string that starts with a formula but may have 
	 * more text appended to the end of the formula.
	 */
	public static String nextFormula(String formulaText) {
		int count= 0;
		final int max= formulaText.length()-1;
		for (int i = 0; i <= max; i++) {
			char c = formulaText.charAt(i);
			switch (c) {
			case '-': break;
			case '*': count--; break;
			case 'T': count++; break;
			case 'F': count++; break;
			case '^': {
				count++;
				while (i < max && Character.isDigit(formulaText.charAt(i+1))) 
					i++;
			} break;
			default: throw new RuntimeException("Invalid character '"+c+"' at position "+i+":"+formulaText);
			}
			if (0 < count)
				return formulaText.substring(0, i+1);
		}
		throw new RuntimeException("Not a valid formula:"+formulaText);
	}
	

	private Formula parse(String formula) {
		Stack<Formula> stack= new Stack<Formula>();
		for (int i = formula.length(); 0 < i--;) {
			char c = formula.charAt(i);
			if (Operator.isNegation(c)) {
				Formula f= stack.pop();
				String text= formula.substring(i, i + f.length()+1);
				stack.push(createNegation(f, text));
			}
			else if (Operator.isImplication(c)) {
				Formula antecendent= stack.pop();
				Formula consequent= stack.pop();
				String text= formula.substring(i, i + antecendent.length()+consequent.length()+1);
				stack.push(createImplication(antecendent, consequent, text));
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
				if (i <= 0 || formula.charAt(i-1) != '^')  
					throw new RuntimeException("Expected a '^' at position "+((0 < i) ? (i-1) : 0));
				String text= formula.substring(--i, end);
				stack.push(createVariable(text));
			}
			else 
				throw new RuntimeException("Unknown symbol:"+c);
		}
		
		if (stack.size() != 1) 
			throw new RuntimeException("Invalid postcondition after evaluating formula wellformedness: count < 1");
		
		Formula formula2= stack.pop();
		return formula2;
	}

	/**
	 * Creates a new formula by making the given substitutions for the 
	 * variables in the given formula.  
	 */
	public Formula createFormula(Formula templateFormula, HashMap<Variable, Formula> substitutions) {
		
		if (templateFormula instanceof Constant) 
			return templateFormula;
		
		if (templateFormula instanceof Variable) {
			Formula f= substitutions.get(templateFormula);
			if (f != null)
				return f;
			return templateFormula;
		}
		
		if (templateFormula instanceof Negation) {
			Formula child= ((Negation)templateFormula).getChild();
			Formula f= createFormula(child, substitutions);
			if (f == child)
				return templateFormula;
			return createNegation(f);
		}
		
		Implication implication= (Implication)templateFormula;
		Formula ant= implication.getAntecedent();
		Formula con= implication.getConsequent();
		Formula a= createFormula(ant, substitutions);
		Formula c= createFormula(con, substitutions);
		if (a != ant || c != con)
			return createImplication(a, c);
		return templateFormula;
	}

}
