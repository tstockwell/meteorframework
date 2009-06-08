package net.sf.meteor.storage.triplestore.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.meteor.storage.triplestore.TripleStoreCodec;
import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.utils.Logging;

/**
 * Implementation of a simple JDBC triple store.
 * 
 * All data is stored as strings.
 * The TripleStoreCodec class is responsible for encoding numeric data so that 
 * stored data preserves its natural order.
 * 
 * I suppose I could save a lot of space by tokenizing subject and predicate 
 * values as integers.  
 * However, there is a tradeoff for runtime speed with programatic complexity.
 * For now I decided to KISS.
 * 
 * @author Ted Stockwell
 *
 */
@SuppressWarnings("unchecked")
public class TripleStoreImpl {
	
	private static final String INSERT_STATEMENT = "INSERT INTO STATEMENTS (SUBJECT, PREDICATE, PREDICATE_KEY, VALUE) VALUES (''{0}'',''{1}'',''{2}'',''{3}'')";
	private static final String CREATE_STATEMENTS_TABLE= "CREATE TABLE STATEMENTS (SUBJECT VARCHAR NOT NULL, PREDICATE VARCHAR NOT NULL, PREDICATE_KEY VARCHAR, VALUE VARCHAR )";
	private Connection _connection;
	private List<String> _cachedStatements= new ArrayList<String>(); 
	
	private @Inject TripleStoreCodec _codec;
	private @Inject Scope _scope;
	
	TripleStoreImpl(Scope modelRepository, Connection connection) throws SQLException {
		_connection= connection;		
		modelRepository.injectMembers(this);
		createTables();
	}
	
	Connection getJDBCConnection() {
		return _connection;
	}
	
	synchronized public void close() {
		// do nothing
	}

	synchronized public void createTables() 
	throws SQLException 
	{
		DatabaseMetaData metaData= _connection.getMetaData();
		ResultSet rs= metaData.getTables(null, null, "STATEMENTS", null);
		if (!rs.next()) {
			Statement s= _connection.createStatement();
			try {
				s.execute(CREATE_STATEMENTS_TABLE);
			}
			finally {
				try { s.close(); } catch (Throwable t) { }
			}
		}
		rs.close();
	}
	
	
	public void persist(Resource resource) {
		HashSet<String> completedURIs= new HashSet<String>();
		ArrayList<String> statements= new ArrayList<String>();
		persist(resource, statements, completedURIs);
		synchronized (_cachedStatements) {
			_cachedStatements.addAll(statements);
		}
	}
	
	private void persist(Resource resource, List<String> statements, HashSet<String> completedURIs) {
		try {
			String resourceURI= resource.getURI();
			completedURIs.add(resourceURI);
			
			Set<Property<?>> properties= resource.getContainedProperties();
			for (Property<?> property : properties) {
				if (property.isTemporal())
					continue;
				
				Object value= resource.getProperty(property);
				if (property.isMany()) {
					if (property.isOrdered()) {
						int i= 0;
						for (Object val : (Collection<?>)value) 
							persistPropertyValue(resourceURI, property, i++, val, statements, completedURIs);
					}
					else if (property.isIndexed()) {
						for (Map.Entry<?, ?> entry : ((Map<?,?>)value).entrySet())  
							persistPropertyValue(resourceURI, property, entry.getKey(), entry.getValue(), statements, completedURIs);
					}
					else {
						for (Object val : (Collection<?>)value) 
							persistPropertyValue(resourceURI, property, null, val, statements, completedURIs);
					}
				}
				else 
					persistPropertyValue(resourceURI, property, null, value, statements, completedURIs);
			}
		} 
		catch (SQLException e) {
			throw new StorageException(e);
		}
	}

	private void persistPropertyValue(String resourceURI, Property<?> property, Object key, Object value, List<String> statements, HashSet<String> completedURIs ) 
	throws SQLException
	{
		String encodedValue= _codec.encode(value);
		String encodedKey= (key == null) ? "" : _codec.encode(key);
		String sql= MessageFormat.format(INSERT_STATEMENT, resourceURI, property.getURI(), encodedKey, encodedValue);
		statements.add(sql);
		
		if (value instanceof Resource && property.isComposite()) {
			Resource resource2= (Resource)value;
			if (!completedURIs.contains(resource2.getURI()))
				persist((Resource)value, statements, completedURIs);
		}
	}

	public void delete(String uri)
	{
		HashSet<String> uris= new HashSet<String>();
		uris.add(uri);
		List<Resource> list= findResources(uris);
		ArrayList<String> statements= new ArrayList<String>();
		HashSet<String> urisToDelete= new HashSet<String>();
		for (Resource resource : list) 			
			findUrisToDelete(resource, urisToDelete);
		for (String uri2Delete: urisToDelete) 
			statements.add("DELETE FROM STATEMENTS where SUBJECT = '"+uri2Delete+"'");
		
		synchronized (_cachedStatements) {
			_cachedStatements.addAll(statements);
		}
	}
	private void findUrisToDelete(Resource resource, Collection<String> uris)
	{
		uris.add(resource.getURI());
		for (Property<?> property : resource.getContainedProperties()) {
			if (property.isComposite()) {
				Collection<Resource> values= resource.getPropertyValues(property.getURI());
				for (Resource value : values)
					findUrisToDelete(value, uris);
			}
		}
	}
	
	

	public List<Resource> list(String sql)
	{
		Statement statement= null;
		ResultSet resultSet= null;
		try
		{
			statement= _connection.createStatement();
			Logging.log(Level.FINE, sql);
			resultSet= statement.executeQuery(sql);
			HashSet<String> matchingURIs= new HashSet<String>();
			while (resultSet.next()) 
				matchingURIs.add(resultSet.getString("SUBJECT"));			
			return findResources(matchingURIs);
		} 
		catch (SQLException e)
		{
			throw new StorageException("Error executing query:"+sql, e);
		}
		finally {
			try { resultSet.close(); } catch (Throwable t) { }
			try { statement.close(); } catch (Throwable t) { }
		}
	}

	public List<Resource> findResources(Set<String> matchingURIs)
	{
		if (matchingURIs == null || matchingURIs.isEmpty())
			return Collections.EMPTY_LIST;
		
		Statement statement= null;
		ResultSet resultSet= null;
		try {

			String sql= "SELECT * FROM STATEMENTS WHERE ";

			boolean first= true;
			for (String uri : matchingURIs) {
				if (!first)
					sql+= " OR ";
				sql+= "SUBJECT = '"+uri+"'";
				first= false;
			}
			sql+= " ORDER BY SUBJECT, PREDICATE, PREDICATE_KEY";
			Logging.fine(sql);

			statement= _connection.createStatement();			
			resultSet= statement.executeQuery(sql);

			HashMap<String, Resource> resultsByURI= new HashMap<String, Resource>();
			while (resultSet.next()) {
				String subject= resultSet.getString("SUBJECT");
				String propertyURI= resultSet.getString("PREDICATE");
				String propertyKey= resultSet.getString("PREDICATE_KEY");
				String stringValue= resultSet.getString("VALUE");
				
				Resource t= resultsByURI.get(subject);
				if (t == null) {
					t= _scope.createInstance(Resource.class);
					t.setURI(subject);
					resultsByURI.put(subject, t);
				}

				// decode value 
				Object value= stringValue;
				Property<?> property= _scope.findResourceByURI(propertyURI, Property.class);
				if (property != null) {
					if (property.isReference()) 
					{
						Object object= _scope.findResourceByURI(stringValue);
						if (object != null) {
							value= object;
						}
						else if ((object= findResource(stringValue)) != null)
							value= object;
					}
					else
						value= _codec.decode(stringValue, property.getRange().getJavaType());
				}

				//						if (property != null && property.isIndexed()) {
				//							Map map= t.getProperty(propertyURI);
				//							Object key= _codec.decode(propertyKey, property.getIndexedType().getJavaType());
				//							map.put(key, value);
				//						}
				//						else
				Object key= null;
				if (propertyKey != null && property != null && property.isIndexed())
					key= _codec.decode(propertyKey, property.getIndexedType().getJavaType());				
				t.setProperty(propertyURI, value, key);
			} 

			ArrayList<Resource> resultList= new ArrayList<Resource>();
			for (String uri : matchingURIs) {
				Resource resource= resultsByURI.get(uri);
				if (resource == null)
					throw new MeteorNotFoundException(uri);
				resultList.add(resource);
			}
			return resultList;
		} 
		catch (SQLException e) {
			throw new StorageException(e);
		}
		finally {
			try {resultSet.close(); } catch (Throwable t) { }
			try {statement.close(); } catch (Throwable t) { }
		}
	}

	public Resource findResource(String stringValue)
	{
		HashSet<String> matchingURIs= new HashSet<String>(1);
		matchingURIs.add(stringValue);
		List<Resource> list= findResources(matchingURIs);
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public void flush() {
		final ArrayList<String> statements= new ArrayList<String>();
		synchronized (_cachedStatements) {
			statements.addAll(_cachedStatements);
			_cachedStatements.clear();
		}
		Statement statement= null;
		try
		{
			Logging.fine(new Logging.Task() {
				public void log(Logger logger) {
					String msg= "";
					boolean first= true;
					for (String statement : statements) {
						if (!first)
							msg+= "\n";
						msg+= statement;
					}
					Logging.fine(msg);
				}
			});
			
			statement= _connection.createStatement();
			for (String sql : statements) 
				statement.addBatch(sql);
			statement.executeBatch();
		} 
		catch (SQLException e)
		{
			throw new StorageException("Error executing flush.", e);
		}
		finally {
			try { statement.close(); } catch (Throwable t) { }
		}
	}

}
