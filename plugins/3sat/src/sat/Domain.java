package sat;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.TreeMap;

import sat.Formula.Operator;

public class Domain {
	
	/*
	 * For efficiency we (weakly) cache the formulas.
	 */
	final private Map<String, Reference<Formula>> __cache= new TreeMap<String, Reference<Formula>>(); 
	final private ReferenceQueue<Formula> __referenceQueue= new ReferenceQueue<Formula>();
	private static class FormulaReference extends WeakReference<Formula> {
		String _string;
		public FormulaReference(Formula referent) {
			super(referent, __referenceQueue);
			_string= referent._string;
		}
	}
	
	// constants
	transient public static final Formula TRUE= new Formula('T');
	transient public static final Formula FALSE= new Formula('F');
	
	// variables
	transient public static final Formula[] VARIABLES= new Formula[MAX_VARIABLES];
	static {
		__cache.put(TRUE.getFormulaText(), new FormulaReference(TRUE));
		__cache.put(FALSE.getFormulaText(), new FormulaReference(FALSE));
	}
	
	
	public static final Domain getDomain() {
		if (__domain == null)
			throw new RuntimeException("Before any clas or methods in this package may be used the Domain.init method mus be called");
		return __domain;
	}
	
	public static void init(int variableCount) {
		if (__domain != null)
			throw new RuntimeException("The domain has already been initialized, only one domain per JVM/Classloader is supported.");
		__domain= new Domain(variableCount);
		for (int i= 1; i <= variableCount; i++) {
			Formula f= VARIABLES[i]= Formula.create(i);
			__cache.put(f.getFormulaText(), new FormulaReference(f));
		}
	}
	
	private int _variableCount;

	private Domain(int count) { 
		_variableCount= count;
	}
	
	public int getVariableCount() {
		return _variableCount;
	}

	public static Formula create(String path) {
		Formula formula= null;
		synchronized (__cache) {
			Reference<Formula> ref= __cache.get(path);
			if (ref == null || (formula= ref.get()) == null) {
				formula= parse(path);
				__cache.put(path, new FormulaReference(formula));
			}
		}
		
		// clean up expired references
		FormulaReference ref;
		while ((ref= (FormulaReference)__referenceQueue.poll()) != null) {
			synchronized (__cache) {
				__cache.remove(ref._string);
			}
		}
		
		return formula;
	}

	public static Formula createNegation(Formula right) {
		return create(Operator.Negation+right._string);
	}

	public static Formula createImplication(Formula antecedent, Formula consequent) {
		return create(Operator.Implication+antecedent._string+consequent._string);
	}

	public static final int variableCount() {
		return getDomain()._variableCount;
	}
}
