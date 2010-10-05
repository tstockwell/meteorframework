package sat.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import sat.Formula;
import sat.PropositionalSystem;

/**
 * Reads a clausal SAT problem from a file.
 * File format described here: 
 * 		http://www.satcompetition.org/2004/format-solvers2004.html
 * 
 * @author Ted Stockwell <emorning@yahoo.com>
 */
public class CNFFile {

	public static CNFFile readAndReduce(PropositionalSystem system, InputStream inputStream, Solver solver)
	 throws IOException
	{
		CNFFile cnfFile= new CNFFile();		
		BufferedReader reader= new BufferedReader(new InputStreamReader(inputStream));
		String inputLine;
		while ((inputLine= reader.readLine()) != null) {
			
			if (inputLine.startsWith("c"))
				continue;
			
			String[] tokens= inputLine.split(" ");
			if (tokens[0].equals("p")) {
				cnfFile._variableCount= Integer.parseInt(tokens[2]);
				cnfFile._clauseCount= Integer.parseInt(tokens[3]);
				break;
			}
			
			throw new RuntimeException("Expected to find 'p' line before clauses");
		}
		
		// read all clauses and append into one big formula
		Formula formula= null;
		for (int count= 0; count < cnfFile._clauseCount; count++) { 
			System.out.println("processing clause "+count);
			if ((inputLine= reader.readLine()) == null) 
					throw new RuntimeException("Premature end of file");
			
			// get all variables in the clause
			String[] tokens= inputLine.split(" ");
			if (tokens.length <= 1) // an empty clause
				continue;
			
			// prefix variable symbols with '^'
			for (int t= 0; t < tokens.length-1; t++) {
				tokens[t]= tokens[t].startsWith("-")?
					 "-^"+tokens[t].substring(1): 
					 "^"+tokens[t];
			}
			
			// create clause
			Formula clause= system.createFormula(tokens[0]);
			for (int v= 1; v < tokens.length-1; v++) 
				clause= system.createImplication(
							system.createNegation(clause),
							system.createFormula(tokens[v]));
				
			// add clause to formula
			if (formula== null) {
				formula= clause;
			}
			else
				formula= system.createNegation(
							system.createImplication(
								formula,
								system.createNegation(clause)));
			
			formula= solver.reduce(formula);
		}
		
		cnfFile._formula= formula;
		return cnfFile;
	}
	
	public static CNFFile read(PropositionalSystem system, InputStream inputStream) throws IOException 
	{
		return readAndReduce(system, inputStream, Solver.FAUX_SOLVER);
	}
	
	private Formula _formula;
	private int _variableCount;
	private int _clauseCount;
	
	private CNFFile() { }

	public Formula getFormula() {
		return _formula;
	}
	
	public int getVariableCount() {
		return _variableCount;
	}

	public int getClauseCount() {
		return _clauseCount;
	}
}
