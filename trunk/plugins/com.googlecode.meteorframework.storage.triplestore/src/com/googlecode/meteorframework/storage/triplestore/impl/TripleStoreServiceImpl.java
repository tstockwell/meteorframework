package com.googlecode.meteorframework.storage.triplestore.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.WeakHashMap;


import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.DelegatingConnection;

import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.jdbc.JDBCDriverRegistry;
import com.googlecode.meteorframework.storage.triplestore.TripleStoreConfiguration;
import com.googlecode.meteorframework.storage.triplestore.TripleStoreService;
import com.googlecode.meteorframework.storage.triplestore.TripleStoreSession;
import com.googlecode.meteorframework.utils.Logging;

@Decorator public abstract class TripleStoreServiceImpl 
implements TripleStoreService, Resource 
{
	
	String _jdbcURL; // URL of JDBC database to use
	BasicDataSource _dataSource;
	WeakHashMap<Connection, WeakReference<TripleStoreImpl>> _impls= new WeakHashMap<Connection, WeakReference<TripleStoreImpl>>();
	ReferenceQueue<TripleStoreImpl> _queue= new ReferenceQueue<TripleStoreImpl>();
	
	@Inject TripleStoreConfiguration _configuration;
	@Inject StorageSession.Constructor _sessionConstructor;
	@Inject JDBCDriverRegistry _driverRegistry;
	@Inject Scope _scope;
	@Decorates TripleStoreService _self;
	
	
	protected void init() {
		try
		{
			String connectionURL= _self.getConnectionURL();
			_jdbcURL= connectionURL.substring(TripleStoreConfiguration.TRIPLESTORE_PROTOCOL.length());
			if (_jdbcURL.length() <= 0)
				_jdbcURL= _configuration.getDefaultJdbcURL();
			
			_dataSource= new BasicDataSource();
			_dataSource.setMaxActive(_configuration.getMaxConnectionCount());
			Logging.info("Getting JDBC driver for "+_jdbcURL);
			Driver driver= _driverRegistry.findDriver(_jdbcURL);
			_dataSource.setMaxActive(_configuration.getMaxConnectionCount());
			_dataSource.setDriverClassName(driver.getClass().getName());
			_dataSource.setUrl(_jdbcURL);
			_dataSource.setAccessToUnderlyingConnectionAllowed(true);

			// create db tables if they don't already exist
			Connection connection= _dataSource.getConnection();
			try {
				new TripleStoreImpl(_scope, connection).createTables();
			}
			finally {
				try { connection.close(); } catch (Throwable t) { }
			}
		} 
		catch (SQLException e)
		{
			throw new MeteorException(e);
		}
	}
	
	
	@Override public StorageSession openSession() 
	throws StorageException 
	{
		if (_dataSource == null)
			init();
		try {
			DelegatingConnection connection= (DelegatingConnection)_dataSource.getConnection();
			TripleStoreImpl storeImpl= new TripleStoreImpl(_scope, connection);
			TripleStoreSession  session= _scope.createInstance(TripleStoreSession.class);
			session.init(this, storeImpl);
			return session;
		} catch (SQLException e) {
			throw new StorageException(e);
		}
	}

	@Override public void close()
	{
		try { _dataSource.close(); } catch (Throwable t) { }
	}
}
