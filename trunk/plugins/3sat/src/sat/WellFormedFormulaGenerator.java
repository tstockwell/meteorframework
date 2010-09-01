package sat;

import java.io.File;
import java.io.PrintWriter;


/**
 * This class generates all well-formed propositional formula of 1 variable and 
 * two operators ( the 'if-then' operator, and the 'negation' operator) and 
 * saves the formulas in a text file names 'wellFormedForumulas.txt in the current 
 * working directory.
 * This program uses the characters 'v','*', and '-' where
 * 'v' is a variable, '*" is the if-then operator, and '-' is the negation operator.
 * The formulas are in Polish notation.
 * Examples...
 * v
 * -v
 * --v
 * *vv
 * -*vv
 * *-vv
 * *v-v
 * --*vv
 * 
 * The formulas are listed in 'lexical' order.  
 * The lexical order is the order in which they would be listed if included in a 
 * dictionary.
 * The lexical order of the symbols is '-', then '*', then 'v'.
 * Note that the examples above are in lexical order.  
 * 
 * 
 * This programs generates all formulas up to 100 characters in length.
 * 
 * @author Ted Stockwell
 */
public class WellFormedFormulaGenerator implements Runnable {

	public static void main(String[] args) {
		new WellFormedFormulaGenerator().run();
	}

	public void run() {
		PrintWriter writer= null;

		try {
			File file= new File("wellFormedFormulas.txt");
			writer= new PrintWriter(file);
			System.out.println("Creating file "+file.getCanonicalPath());

			String formula = "v";

			while (formula.length() <= 100) {
				writer.println(formula);
				formula= getNextWellFormedFormula(formula);
			}
			
			writer.close();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.flush();
				writer.close();
			}
			catch (Throwable t) {				
			}
		}
	}
	public String getNextWellFormedFormula(String formula) {
		do {
			int formulaLength= formula.length();
			getNext : {
					// increment to next formula				
					for (int i= formulaLength; 0 < i--;) {
						char c= formula.charAt(i);
						if (c != 'v') {
							char nextChar= getNextChar(c);
							formula= formula.substring(0, i)+nextChar+pad('-', formulaLength - i - 1);
							break getNext;
						}
					}
					// if we get to here then the formula starts with all 'v''s
					formula= pad('-', formulaLength+1);
			}
		}
		while (!isValid(formula));
		
		return formula;
	}
	
	private static final char getNextChar(char c) {
		switch (c) {
		case 'v':
			return '-';
		case '-':
			return '*';
		case '*':
			return 'v';
		default:
			throw new RuntimeException("Unknown symbol:"+c);
		}
	}
	private static final String pad(char c, int count) {
		if (count <= 0)
			return "";
		char[] ds= new char[count];
		for (int i= count; 0 < i--;)
			ds[i]= c;
		return new String(ds);
	}
	/**
	 * Determines if a formula is well-formed.
	 */
	private static boolean isValid(String formula) {
		
		char[] symbols= formula.toCharArray();
		int count= 0;
		for (int i = symbols.length; 0 < i--;) {
			char c = symbols[i];
			if (c == 'v') {
				count++;
			}
			else if (c == '-') {
				if (count < 1)
					return false; // error
			}
			else if (c == '*') {
				if (count < 2)
					return false; // error
				count--;
			}
			else
				throw new RuntimeException("Unknown symbol:"+c);
		}
		
		if (1 < count) {
			return false;
		}
		else if (count < 1)
			throw new RuntimeException("Invalid postcondition after evaluating formula wellformedness: count < 1");
		
		return true;
		
		
//		While there are input tokens left 
//		Read the next token from input. 
//		If the token is a value 
//		Push it onto the stack. 
//		Otherwise, the token is a function. (Operators, like +, are simply functions taking two arguments.) 
//		It is known that the function takes n arguments. 
//		If there are fewer than n values on the stack 
//		(Error) The user has not input sufficient values in the expression. 
//		Else, Pop the top n values from the stack. 
//		Evaluate the function, with the values as arguments. 
//		Push the returned results, if any, back onto the stack. 
//		If there is only one value in the stack 
//		That value is the result of the calculation. 
//		If there are more values in the stack 
//		(Error) The user input too many values. 
		
	}


}
