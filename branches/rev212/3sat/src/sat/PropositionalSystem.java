package sat;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * A propositional system manages the creation and storage of propositional
 * formulas.
 * 
 * This implementation builds an internal <a
 * href="http://en.wikipedia.org/wiki/Trie">trie</a> structure for representing
 * all the given formulas. A trie is used because it is able to store many
 * formulas efficiently and provides a fast way to search formulas for
 * substitution instances.
 * 
 * @see InstanceRecognizer
 * 
 *      A Formula is really a leaf node in the trie.
 * 
 *      The trie is also designed to be space efficient by allowing Formula that
 *      are no longer used to be garbage collected and the associated references
 *      in the trie to be removed when formulas are collected. The nodes of the
 *      trie have strong references to their parents and soft references to
 *      their children. If some other object does not hold a strong reference to
 *      a leaf formula then the Formula will eventually be garbage collected.
 *      When a Formula is finalized it will remove itself from the trie, thus
 *      making it possible for parent nodes to also be garbage collected.
 * 
 *      Use the PropositionalSystem.create* methods to create formulas.
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
public class PropositionalSystem {

	final Formula _root = new Formula("", null);

	final Formula _true = _root.addFormula(Symbol.True.getFormulaText());
	final Formula _false = new Formula(Symbol.False.getFormulaText(), _root);

	public PropositionalSystem() {
	}

	public Formula getTrue() {
		return _true;
	}

	public Formula getFalse() {
		return _false;
	}

	/**
	 * Create a formula from its textual representation in normal form
	 */
	public Formula createFormula(String path) {
		return _root.addFormula(path);
	}

	public Formula createNegation(Formula right) {
		return _root.addFormula(Symbol.Negation.getFormulaText()
				+ right.getFormulaText());
	}

	public Formula createImplication(Formula antecedent, Formula consequent) {
		return _root.addFormula(Symbol.Implication.getFormulaText()
				+ antecedent.getFormulaText() + consequent.getFormulaText());
	}

	public Formula createVariable(int variable) {
		if (variable < 1)
			throw new RuntimeException("Variable numbers must be greater than 0");
		return _root.addFormula(Symbol.Variable.getFormulaText() + variable);
	}

	public Formula getAntecedent(Formula parent) {
		Formula formula = null;
		if (parent._antecedent == null || (formula = parent._antecedent.get()) == null) {
			String formulaText = PropositionalSystem.nextFormula(parent.getFormulaText().substring(1));
			formula = _root.addFormula(formulaText);
			parent._antecedent = new SoftReference<Formula>(formula);
		}
		return formula;
	}

	public Formula getConsequent(Formula parent) {
		Formula formula = null;
		if (parent._consequent == null || (formula = parent._consequent.get()) == null) {
			String formulaText = parent.getFormulaText();
			String antecedentText = PropositionalSystem.nextFormula(formulaText.substring(1));
			formula = _root.addFormula(formulaText.substring(antecedentText.length() + 1));
			parent._consequent = new SoftReference<Formula>(formula);
		}
		return formula;
	}

	/**
	 * This method finds the end of a formula.
	 * 
	 * @param formulaText
	 *            A string that starts with a formula but may have more text
	 *            appended to the end of the formula.
	 */
	public static String nextFormula(String formulaText) {
		int count = 0;
		final int max = formulaText.length() - 1;
		for (int i = 0; i <= max; i++) {
			char c = formulaText.charAt(i);
			switch (c) {
			case '-':
				break;
			case '*':
				count--;
				break;
			case 'T':
				count++;
				break;
			case 'F':
				count++;
				break;
			case '^': {
				count++;
				while (i < max && Character.isDigit(formulaText.charAt(i + 1)))
					i++;
			}
				break;
			default:
				throw new RuntimeException("Invalid character '" + c + "' at position " + i + ":" + formulaText);
			}
			if (0 < count)
				return formulaText.substring(0, i + 1);
		}
		throw new RuntimeException("Not a valid formula:" + formulaText);
	}

	/**
	 * Creates a new formula by making the given substitutions for the variables
	 * in the given formula.
	 */
	public Formula createInstance(Formula templateFormula, HashMap<String, Formula> substitutions) {

		String instanceText = "";
		Formula formula = templateFormula;
		while (formula != null) {
			if (Symbol.isVariable(formula._symbol)) {
				Formula substitute = substitutions.get(formula._symbol);
				if (substitute != null) {
					instanceText = substitute.getFormulaText() + instanceText;
				} else
					instanceText = formula._symbol + instanceText;
			} else
				instanceText = formula._symbol + instanceText;
			formula = formula._parent;
		}
		return _root.addFormula(instanceText);
	}

}
