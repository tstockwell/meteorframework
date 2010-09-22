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
	
	public static CNFFile read(PropositionalSystem system, InputStream inputStream) throws IOException 
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
		
		for (int count= cnfFile._clauseCount; 0 < count--;) { 
			if ((inputLine= reader.readLine()) == null) 
					throw new RuntimeException("Premature end of file");
			
			String[] tokens= inputLine.split(" ");
			
		}
		
		
		return null;
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