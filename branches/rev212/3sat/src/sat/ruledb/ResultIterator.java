package sat.ruledb;

import java.util.Iterator;

/**
 * An iterator that needs to be explicitly closed because it 
 * holds heavyweight internal resources (like a ResultSet). 
 * @author Ted Stockwell
 */
public interface ResultIterator<T> extends Iterator<T> {
	
	/**
	 * Disposes of any internal resources used by this iterator
	 */
	public void close();
}
