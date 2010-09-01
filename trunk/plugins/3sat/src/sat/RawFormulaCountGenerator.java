package sat;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;

public class RawFormulaCountGenerator implements Runnable {

	public static void main(String[] args) {
		new RawFormulaCountGenerator().run();
	}
	
	BigInteger[] counts= new BigInteger[100];

	public void run() {
		PrintWriter writer= null;

		try {
			File file= new File("rawFormulaCounts.txt");
			writer= new PrintWriter(file);
			System.out.println("Creating file "+file.getCanonicalPath());

			counts[0]= new BigInteger("0");
			counts[1]= new BigInteger("1");
			counts[2]= new BigInteger("1");
			
			for (int i= 3; i < counts.length; i++) {
				BigInteger count= counts[i-1];
				int k= 1;
				for (int j= i-2; 1 <= j; j--) {
					count= count.add(counts[j].multiply(counts[k++]));
				}
				counts[i]= count;
			}
			
			for (int i= 1; i < counts.length; i++)
				writer.println(i+": "+counts[i]);
			
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

}
