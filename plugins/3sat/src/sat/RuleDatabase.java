package sat;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.derby.jdbc.EmbeddedDriver;

public class RuleDatabase {
	
	public static final String LIST_FORMULA_LENGTHS= "-listFormulaLengths";

	public String framework = "embedded";
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	public String dbFolder = "/"+Formula.MAX_VARIABLES+"satdb";
	public String options = ";create=true";
	
	public String dbURL= protocol+dbFolder+options;
	Connection _connection;
	
	Integer _lengthOfLongestCanonical= null;
	HashMap<TruthTable, Integer> _lengthOfCanonicalFormulas= null;
	HashMap<Integer, List<Formula>> _canonicalFormulasByLength= new HashMap<Integer, List<Formula>>();
	HashMap<Integer, List<Formula>> _allNonCanonicalFormulasByLength= new HashMap<Integer, List<Formula>>();
	
	
	public static void main(String[] args)
	throws Exception
	{
		if (LIST_FORMULA_LENGTHS.equals(args[0])) {
			RuleDatabase database= new RuleDatabase();
			database.getLengthOfCanonicalFormulas(new TruthTable("0"));
			HashMap<TruthTable, Integer> lengths= database._lengthOfCanonicalFormulas;
			System.out.println("TRUTH VALUE   LENGTH");
			System.out.println("-----------   ------");
			for (int truthValue= 0; truthValue <= 255; truthValue++) {
				String t= "           "+truthValue;
				t= t.substring(t.length()-11);
				Integer length= lengths.get(truthValue);
				if (length == null) {
					System.out.println(t+"   *unknown*");
				}
				else {
					String l= "      "+length;
					l= l.substring(l.length()-6);
					System.out.println(t+"   "+l);
				}
			}
		}
	}
	
	public RuleDatabase() throws SQLException {
		connect();
		createIfNecessary();		
	}

	private void connect() throws SQLException {
		// Load the derby JDBC driver
		new EmbeddedDriver();

		Properties props = new Properties();
		_connection= DriverManager.getConnection(dbURL, props);
		_connection.setAutoCommit(true);
	}

	private void createIfNecessary() throws SQLException {
			DatabaseMetaData metaData= _connection.getMetaData();
			ResultSet resultSet= metaData.getTables(null, null, "FORMULA", null);
			if (resultSet.next() == false) {
				Statement s = _connection.createStatement();

				s.execute("create table FORMULA(" +
						"ID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
						"FORMULA long varchar NOT NULL, " +
						"LENGTH int NOT NULL, " +
						"TRUTHVALUE long varchar NOT NULL, " +
						"CANONICAL int not NULL, " +
						"PRIMARY KEY (ID)" +
						")");
				s.execute("CREATE INDEX formula_index_1 ON FORMULA (LENGTH, ID)");
//				s.execute("CREATE INDEX formula_index_2 ON FORMULA (TRUTHVALUE, ID)");
//				s.execute("CREATE INDEX formula_index_3 ON FORMULA (TRUTHVALUE, CANONICAL)");
				s.execute("CREATE INDEX formula_index_4 ON FORMULA (CANONICAL, LENGTH, ID)");
				

//				s.execute("create table RULE(" +
//						"ID integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
//						"ANTECEDENT integer NOT NULL, " +
//						"CONSEQUENT integer not null, " +
//						"PRIMARY KEY (ID)" +
//						")");
//				s.execute("CREATE INDEX rule_index_1 ON RULE (ANTECEDENT, CONSEQUENT)");
//				s.execute("CREATE INDEX rule_index_2 ON RULE (CONSEQUENT, ANTECEDENT)");
				
				s.close();
			}
			resultSet.close();
	}

	public Formula getLastGeneratedFormula() 
	{
		try {
			String sql= "SELECT * FROM FORMULA ORDER BY ID DESC";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				resultSet= s.executeQuery(sql);
				if (resultSet.next()) {
					String formula= resultSet.getString("FORMULA");
					return Formula.parseFormula(formula);
				}
			}
			finally {
				try { resultSet.close(); } catch (Throwable t) { }
				try { s.close(); } catch (Throwable t) { }
			}
			return null;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Formula> getCanonicalFormulas(TruthTable truthTable) {
		try {
			String sql= "SELECT * FROM FORMULA WHERE TRUTHVALUE = '"+truthTable+"' ORDER BY ID ASC";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				resultSet= s.executeQuery(sql);
				ArrayList<Formula> list= new ArrayList<Formula>();
				while (resultSet.next()) {
					String formula= resultSet.getString("FORMULA");
					list.add(Formula.parseFormula(formula));
				}
				return list;
			}
			finally {
				try { resultSet.close(); } catch (Throwable t) { }
				try { s.close(); } catch (Throwable t) { }
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	public void shutdown() {
		
		try {
			if (_connection != null)
				_connection.close();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}

		/*
		       In embedded mode, an application should shut down Derby.
		       If the application fails to shut down Derby explicitly,
		       the Derby does not perform a checkpoint when the JVM shuts down, which means
		       that the next connection will be slower.
		       Explicitly shutting down Derby with the URL is preferred.
		       This style of shutdown will always throw an "exception".
		 */
		if (framework.equals("embedded"))
		{
			try {
				if (_connection != null)
					DriverManager.getConnection("jdbc:derby:;shutdown=true");
			}
			catch (SQLException se) {
			}

		}
	}

	public int getLengthOfLongestCanonicalFormula() {
		if (_lengthOfLongestCanonical == null) {
			try {
				String sql= "SELECT * FROM FORMULA WHERE CANONICAL = 1 ORDER BY LENGTH DESC";
				Statement s = _connection.createStatement();
				ResultSet resultSet= s.executeQuery(sql);
				if (resultSet.next()) {
					_lengthOfLongestCanonical= resultSet.getInt("LENGTH");
				}
				else
					_lengthOfLongestCanonical= 0;
				System.out.println("The length of the longest canonical formula is "+_lengthOfLongestCanonical);
				try { s.close(); } catch (Throwable t) { }
				try { resultSet.close(); } catch (Throwable t) { }
			} 
			catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return _lengthOfLongestCanonical;
	}

	/**
	 * Formulas longer than this length are guaranteed to be reducable with rules,
	 * generated from previous formulas. 
	 * Therefore processing can stop when formulas get this long.
	 */
	public int lengthOfLongestPossibleNonReducableFormula() {
		int maxLength= getLengthOfLongestCanonicalFormula();
		if (maxLength <= 0) // we don't know the length of longest formula yet
			return Integer.MAX_VALUE;
		return maxLength*2+1;
	}
	

	public ResultIterator<Formula> findCanonicalFormulasByLength(int size) {
		List<Formula> formulas= _canonicalFormulasByLength.get(size);
		if (formulas == null) {
			try {
				String sql= "SELECT * FROM FORMULA WHERE LENGTH = "+size+" AND CANONICAL = 1 ORDER BY ID ASC";
				Statement s = _connection.createStatement();
				ResultSet resultSet= s.executeQuery(sql);
				List<Formula> list= new ArrayList<Formula>();
				while (resultSet.next()) {
					list.add(Formula.parseFormula(resultSet.getString("FORMULA")));
				}
				formulas= list;
				_canonicalFormulasByLength.put(size, formulas);
			} 
			catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		final Iterator<Formula> results= formulas.iterator();
		return new ResultIterator<Formula>() {
			public void close() {
				// do nothing
			}
			public boolean hasNext() {
				return results.hasNext();
			}
			public Formula next() {
				return results.next();
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void addFormula(Formula formula) {
		try {
			String sql= "INSERT INTO FORMULA (FORMULA, LENGTH, TRUTHVALUE, CANONICAL) VALUES ('"+
						formula.toString()+"',"+
						formula.length()+","+
						"'"+formula.getTruthTable()+"',"+
						(formula.isCanonical()?1:0)+")";
			Statement s = _connection.createStatement();
			try {
				s.execute(sql);
				int formulaLength= formula.length();
				if (formula.isCanonical()) {
					if (_lengthOfLongestCanonical == null || _lengthOfLongestCanonical < formulaLength) {
						_lengthOfLongestCanonical= formula.length();
						System.out.println("The length of the longest canonical formula is "+_lengthOfLongestCanonical);
					}
					_lengthOfCanonicalFormulas.put(formula.getTruthTable(), formulaLength);
					List<Formula> formulas= _canonicalFormulasByLength.get(formulaLength);
					if (formulas == null) {
						formulas= new ArrayList<Formula>();
						_canonicalFormulasByLength.put(formulaLength, formulas);
					}
					formulas.add(formula);
				}
				else {
					for (int l= formulaLength; 0 < l; l--) {
						List<Formula> formulas= _allNonCanonicalFormulasByLength.get(l);
						if (formulas == null) {
							formulas= new ArrayList<Formula>();
							_allNonCanonicalFormulasByLength.put(l, formulas);
						}
						formulas.add(formula);
					}
				}
			}
			finally {
				try { s.close(); } catch (Throwable t) { }
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public ResultIterator<Formula> getAllNonCanonicalFormulas(int maxLength) {
		List<Formula> formulas= _allNonCanonicalFormulasByLength.get(maxLength);
		if (formulas == null) {
			try {
				String sql= "SELECT * FROM FORMULA WHERE CANONICAL = 0 AND LENGTH <= "+maxLength;
				Statement s = _connection.createStatement();
				ResultSet resultSet= s.executeQuery(sql);
				List<Formula> list= new ArrayList<Formula>();
				while (resultSet.next()) {
					list.add(Formula.parseFormula(resultSet.getString("FORMULA")));
				}
				formulas= list;
				_allNonCanonicalFormulasByLength.put(maxLength, formulas);
			} 
			catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		final Iterator<Formula> results= formulas.iterator();
		return new ResultIterator<Formula>() {
			public void close() {
				// do nothing
			}
			public boolean hasNext() {
				return results.hasNext();
			}
			public Formula next() {
				return results.next();
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public int getLengthOfCanonicalFormulas(TruthTable truthTable) {
		if (_lengthOfCanonicalFormulas == null) {
			try {
				HashMap<TruthTable, Integer> lengths= new HashMap<TruthTable, Integer>();
				String sql= "SELECT LENGTH, TRUTHVALUE FROM FORMULA WHERE CANONICAL=1 ORDER BY ID ASC";
				Statement s = _connection.createStatement();
				ResultSet resultSet= s.executeQuery(sql);
				while (resultSet.next()) {
					TruthTable tt= new TruthTable(resultSet.getString("TRUTHVALUE"));
					if (!lengths.containsKey(tt))
						lengths.put(tt, resultSet.getInt("LENGTH"));
				}
				s.close();
				_lengthOfCanonicalFormulas= lengths;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		Integer length= _lengthOfCanonicalFormulas.get(truthTable);
		if (length == null)
			return -1;
		return length;
	}

}
