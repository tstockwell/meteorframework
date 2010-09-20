package sat.ruledb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.derby.jdbc.EmbeddedDriver;

import sat.PropostionalSystem;
import sat.Formula;
import sat.TruthTable;


/**
 * An API for accessing the rule database.
 * @author Ted Stockwell
 *
 */
public class RuleDatabase {
	
	public class Navigator {
		
		ResultSet _resultSet;
		private Boolean _next;
		
		public Navigator() {
			try {
				_selectCanonical.setString(1, "");
				_resultSet= _selectCanonical.executeQuery();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public boolean hasNext() {
			try {
				if (_next == null) 
					_next= _resultSet.next() ? Boolean.TRUE : Boolean.FALSE;							
				return _next;
			}
			catch (SQLException x) { throw new RuntimeException(x); }
		}

		public Formula next() {
			try {
				if (_next == null)
					_resultSet.next();
				_next= null;
				return Formula.createFormula(_resultSet.getString("FORMULA"));
			}
			catch (SQLException x) { throw new RuntimeException(x); }
		}

		public void close() {
			try { _resultSet.close(); } catch (SQLException x) { throw new RuntimeException(x); }
		}

		public void advanceFromPosition(int i) {
			try {
				String formula= _resultSet.getString("FORMULA").substring(0, i+1)+((char)0x7F);
				_resultSet.close();
				_selectCanonical.setString(1, formula);
				_resultSet= _selectCanonical.executeQuery();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}
	
	public static final String LIST_FORMULA_LENGTHS= "-listFormulaLengths";

	public String framework = "embedded";
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
	public String dbFolder = "/"+PropostionalSystem.variableCount()+"satdb";
	public String options = ";create=true";
	
	public String dbURL= protocol+dbFolder+options;
	Connection _connection;
	
	Integer _lengthOfLongestCanonical= null;
	HashMap<TruthTable, Integer> _lengthOfCanonicalFormulas= null;
	HashMap<Integer, List<Formula>> _canonicalFormulasByLength= new HashMap<Integer, List<Formula>>();
	//HashMap<Integer, List<Formula>> _allNonCanonicalFormulasByLength= new HashMap<Integer, List<Formula>>();
	
	
	private PreparedStatement _selectCanonical;
	
	public RuleDatabase() throws SQLException {
		connect();
		createIfNecessary();		
		
		String sql= "SELECT FORMULA FROM FORMULA WHERE CANONICAL = 0 AND ? < FORMULA ORDER BY FORMULA";
		_selectCanonical= _connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, Connection.TRANSACTION_READ_UNCOMMITTED);
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
				s.execute("CREATE INDEX formula_index_4 ON FORMULA (CANONICAL, LENGTH, TRUTHVALUE)");
				

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
					return Formula.createFormula(formula);
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
			String sql= "SELECT * FROM FORMULA WHERE TRUTHVALUE = '"+truthTable+"' AND CANONICAL=1 ORDER BY ID ASC";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				resultSet= s.executeQuery(sql);
				ArrayList<Formula> list= new ArrayList<Formula>();
				while (resultSet.next()) {
					String formula= resultSet.getString("FORMULA");
					list.add(Formula.createFormula(formula));
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
					list.add(Formula.createFormula(resultSet.getString("FORMULA")));
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

	public void addFormula(Formula formula, boolean isCanonical) {
		try {
			String sql= "INSERT INTO FORMULA (FORMULA, LENGTH, TRUTHVALUE, CANONICAL) VALUES ('"+
						formula.getFormulaText()+"',"+
						formula.length()+","+
						"'"+formula.getTruthTable()+"',"+
						(formula.isCanonical()?1:0)+")";
			Statement s = _connection.createStatement();
			try {
				s.execute(sql);
				int formulaLength= formula.length();
				if (isCanonical) {
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
			try {
				String sql= "SELECT * FROM FORMULA WHERE CANONICAL = 0 AND LENGTH <= "+maxLength;
				Statement s = _connection.createStatement();
				final ResultSet resultSet= s.executeQuery(sql);
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
							return Formula.createFormula(resultSet.getString("FORMULA"));
						}
						catch (SQLException x) { throw new RuntimeException(x); }
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			} 
			catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
	}
	public ResultIterator<Formula> getAllNonCanonicalFormulas() {
		try {
			String sql= "SELECT * FROM FORMULA WHERE CANONICAL = 0 ORDER BY FORMULA";
			Statement s = _connection.createStatement();
			final ResultSet resultSet= s.executeQuery(sql);
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
						return Formula.createFormula(resultSet.getString("FORMULA"));
					}
					catch (SQLException x) { throw new RuntimeException(x); }
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
}

	public int getLengthOfCanonicalFormulas(TruthTable truthTable) {
		if (_lengthOfCanonicalFormulas == null) {
			try {
				HashMap<TruthTable, Integer> lengths= new HashMap<TruthTable, Integer>();
				String sql= "SELECT LENGTH, TRUTHVALUE FROM FORMULA WHERE CANONICAL=1 ORDER BY ID ASC";
				Statement s = _connection.createStatement();
				ResultSet resultSet= s.executeQuery(sql);
				while (resultSet.next()) {
					TruthTable tt= TruthTable.create(resultSet.getString("TRUTHVALUE"));
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
		try {
			String sql= "SELECT * FROM FORMULA WHERE TRUTHVALUE = '"+formula.getTruthTable()+"' AND CANONICAL=1";
			Statement s = _connection.createStatement();
			ResultSet resultSet= null;		
			try {
				resultSet= s.executeQuery(sql);
				if (resultSet.next()) 
					return Formula.createFormula(resultSet.getString("FORMULA"));
				return null;
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

//	public ResultIterator<Formula> getAllNonCanonicalFormulasByDescendingLength() {
//		try {
//			final ResultSet resultSet= _selectCanonicalDescending.executeQuery();
//			return new ResultIterator<Formula>() {
//				private Boolean _next;
//				public void close() {
//					try { resultSet.close(); } catch (SQLException x) { throw new RuntimeException(x); }
//				}
//				public boolean hasNext() {
//					try {
//						if (_next == null) 
//							_next= resultSet.next() ? Boolean.TRUE : Boolean.FALSE;							
//						return _next;
//					}
//					catch (SQLException x) { throw new RuntimeException(x); }
//				}
//				public Formula next() {
//					try {
//						if (_next == null)
//							resultSet.next();
//						_next= null;
//						return Formula.create(resultSet.getString("FORMULA"));
//					}
//					catch (SQLException x) { throw new RuntimeException(x); }
//				}
//				public void remove() {
//					throw new UnsupportedOperationException();
//				}
//			};
//		} 
//		catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}

	public Navigator getNonCanonicalFormulaNavigator() {
		return new Navigator();
	}

}
