package sat.ruledb;

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

import sat.Formula;
import sat.PropositionalSystem;


/**
 * An API for accessing the rule database.
 * @author Ted Stockwell
 *
 */
public class RuleDatabase {
	
	public static final int VARIABLE_COUNT= 3;
	
	
	public static final String LIST_FORMULA_LENGTHS= "-listFormulaLengths";

	public String framework = "embedded";
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	public String dbFolder = "/temp/"+VARIABLE_COUNT+"satdb";
	public String options = ";create=true";
	
	public String dbURL= protocol+dbFolder+options;
	Connection _connection;
	
	Integer _lengthOfLongestCanonical= null;
	HashMap<TruthTable, Integer> _lengthOfCanonicalFormulas= null;
	HashMap<Integer, List<Formula>> _canonicalFormulasByLength= new HashMap<Integer, List<Formula>>();
	//HashMap<Integer, List<Formula>> _allNonCanonicalFormulasByLength= new HashMap<Integer, List<Formula>>();
	
	
	final private PropositionalSystem _system;
	final private TruthTables _truthTables;
	public RuleDatabase(TruthTables tables) throws SQLException {
		_truthTables= tables;
		_system= tables.getSystem();
		connect();
		createIfNecessary();
		
	}

	public PropositionalSystem getSystem() {
		return _system;
	}

	public Formula getLastGeneratedFormula() 
	{
		ResultIterator<Formula> i= executeQuery("SELECT * FROM FORMULA ORDER BY ID DESC");
		if (!i.hasNext())
			return null;
		Formula f= i.next();
		i.close();
		return f; 
	}

	public List<Formula> getCanonicalFormulas(TruthTable truthTable) 
	{
		ResultIterator<Formula> i= executeQuery("SELECT * FROM FORMULA WHERE TRUTHVALUE = '"+truthTable+"' AND CANONICAL=1 ORDER BY ID ASC");
		ArrayList<Formula> list= new ArrayList<Formula>();
		while (i.hasNext()) 
			list.add(i.next());
		i.close();
		return list;
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
			Iterator<Formula> i= executeQuery("SELECT * FROM FORMULA WHERE LENGTH = "+size+" AND CANONICAL = 1 ORDER BY ID ASC");
			List<Formula> list= new ArrayList<Formula>();
			while (i.hasNext()) 
				list.add(i.next());
			formulas= list;
			_canonicalFormulasByLength.put(size, formulas);
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

	public void addFormula(Formula formula, boolean isCanonical) {
		try {
			String formulaText= formula.getFormulaText();
			
			String sql= "INSERT INTO FORMULA (FORMULA, LENGTH, TRUTHVALUE, CANONICAL) VALUES (" +
					"'"+formulaText+"',"+
					formula.length()+","+
					"'"+_truthTables.getTruthTable(formula)+"',"+
					(isCanonical?1:0)+")";
			Statement s = _connection.createStatement();
			try {
				s.execute(sql);
				int formulaLength= formula.length();
				if (isCanonical) {
					if (_lengthOfLongestCanonical == null || _lengthOfLongestCanonical < formulaLength) {
						_lengthOfLongestCanonical= formula.length();
						System.out.println("The length of the longest canonical formula is "+_lengthOfLongestCanonical);
					}
					_lengthOfCanonicalFormulas.put(_truthTables.getTruthTable(formula), formulaLength);
					List<Formula> formulas= _canonicalFormulasByLength.get(formulaLength);
					if (formulas == null) {
						formulas= new ArrayList<Formula>();
						_canonicalFormulasByLength.put(formulaLength, formulas);
					}
					formulas.add(formula);
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
		return executeQuery("SELECT * FROM FORMULA WHERE CANONICAL = 0 AND LENGTH <= "+maxLength);
	}
	public ResultIterator<Formula> getAllNonCanonicalFormulas() {
		return executeQuery("SELECT * FROM FORMULA WHERE CANONICAL = 0 ORDER BY FORMULA");
	}
	public ResultIterator<Formula> getAllCanonicalFormulasInLexicalOrder() {
		return executeQuery("SELECT * FROM FORMULA WHERE CANONICAL = 1 ORDER BY FORMULA");
	}

	public int getLengthOfCanonicalFormulas(TruthTable truthTable) {
		if (_lengthOfCanonicalFormulas == null) {
			try {
				HashMap<TruthTable, Integer> lengths= new HashMap<TruthTable, Integer>();
				String sql= "SELECT LENGTH, TRUTHVALUE FROM FORMULA WHERE CANONICAL=1 ORDER BY ID ASC";
				Statement s = _connection.createStatement();
				ResultSet resultSet= s.executeQuery(sql);
				while (resultSet.next()) {
					TruthTable tt= _truthTables.create(resultSet.getString("TRUTHVALUE"));
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

	/**
	 * Finds the canonical form of the given formula.
	 */
	public Formula findCanonicalFormula(Formula formula) {
		ResultIterator<Formula> i= executeQuery("SELECT * FROM FORMULA WHERE TRUTHVALUE = '"+_truthTables.getTruthTable(formula)+"' AND CANONICAL=1");
		if (!i.hasNext())
			return null;
		Formula f= i.next();
		i.close();
		return f;
	}

	public long countNonCanonicalFormulas() {
		try {
			String sql= "SELECT COUNT(*) as count FROM FORMULA WHERE CANONICAL = 0";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				(resultSet= s.executeQuery(sql)).next();
				return resultSet.getLong(1);
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

	public long countCanonicalFormulas() {
		try {
			String sql= "SELECT COUNT(*) as count FROM FORMULA WHERE CANONICAL = 1";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				(resultSet= s.executeQuery(sql)).next();
				return resultSet.getLong(1);
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
						"FORMULA varchar(32672) NOT NULL, " +
						"LENGTH int NOT NULL, " +
						"TRUTHVALUE varchar(32672) NOT NULL, " +
						"CANONICAL int not NULL, " +
						"PRIMARY KEY (ID)" +
						")");
				s.execute("CREATE INDEX formula_index_1 ON FORMULA (LENGTH, CANONICAL, TRUTHVALUE)");
				s.execute("CREATE INDEX formula_index_2 ON FORMULA (TRUTHVALUE, CANONICAL, LENGTH)");
				s.execute("CREATE INDEX formula_index_3 ON FORMULA (FORMULA, CANONICAL, LENGTH, TRUTHVALUE)");
				s.execute("CREATE INDEX formula_index_5 ON FORMULA (CANONICAL, LENGTH, TRUTHVALUE)");
				s.close();
			}
			resultSet.close();
	}

	private ResultIterator<Formula> executeQuery(String sql) {
		Statement s = null;
		try {
			s = _connection.createStatement();
			return asResultIterator(s.executeQuery(sql));
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally {
			//try { s.close(); } catch (Throwable t) { }
		}
	}
	private ResultIterator<Formula> asResultIterator(final ResultSet resultSet) {
		return new ResultIterator<Formula>() {
			private Boolean _next;
			public void close() {
				try { resultSet.close(); } catch (SQLException x) { throw new RuntimeException(x); }
			}
			public boolean hasNext() {
				try {
					if (_next == null) 
						_next= resultSet.next();							
					return _next;
				}
				catch (SQLException x) { throw new RuntimeException(x); }
			}
			public Formula next() {
				try {
					if (_next == null)
						resultSet.next();
					_next= null;
					return _system.createFormula(resultSet.getString("FORMULA"));
				}
				catch (SQLException x) { throw new RuntimeException(x); }
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	
	public long countCanonicalTruthTables() {
		try {
			String sql= "SELECT COUNT(DISTINCT TRUTH_TABLE) as count FROM FORMULA WHERE CANONICAL = 1";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				(resultSet= s.executeQuery(sql)).next();
				return resultSet.getLong(1);
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

}
