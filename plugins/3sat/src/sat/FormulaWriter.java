package sat;

import java.io.IOException;
import java.io.OutputStream;

public class FormulaWriter {
	
	public void write(OutputStream out, Formula formula) {
		try {
			out.write(asString(formula).getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String asString(Formula formula) {
		
	}
}
