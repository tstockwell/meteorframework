package com.googlecode.meteorframework.storage.impl.jdbc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Set;

import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.jdbc.JDBCDriverDescriptor;
import com.googlecode.meteorframework.storage.jdbc.JDBCDriverRegistry;
import com.googlecode.meteorframework.utils.Logging;

/**
 * A service for obtaining JDBC drivers.
 * This service finds JDBC drivers using metadata in the Meteor repository.
 * To register a driver with this service simply include a Meteor metadata
 * file (meteor.n3) in the plugin that contains the driver and define a
 * JDBCDriverDescriptor instance.
 * Example:
 * <pre>
 * 	@prefix meteor: <meteor:net.sf.meteor.> .
 * 	@prefix jdbc: <meteor:net.sf.meteor.storage.jdbc.> .
 * 	@prefix h2: <meteor:net.sf.meteor.library.h2.> .
 * 	
 * 	h2:H2Driver meteor:Resource.type jdbc:JDBCDriverDescriptor ;
 * 				jdbc:JDBCDriver.protocol "jdbc:h2" ;
 * 				jdbc:JDBCDriver.driverClass "org.h2.Driver" .
 * </pre> 
 * 
 * @author ted stockwell
 */
@Decorator public abstract class JDBCDriverRegistryImpl implements JDBCDriverRegistry {
	
	@Inject Scope _repository;
	
	/**
	 * Returns null if no supporting driver could be found.
	 */
	@Override public Driver findDriver(String url) {
		Driver driver= null;
		try {
			driver= DriverManager.getDriver(url);
		}
		catch (Throwable t) {			
		}
		if (driver == null) {
			Set<JDBCDriverDescriptor> all= findAllDriverDescriptors();
			for (Iterator<JDBCDriverDescriptor> i= all.iterator(); driver == null && i.hasNext(); ) {
				JDBCDriverDescriptor descriptor= i.next();
				if (url.startsWith(descriptor.getProtocol())) {
					String driverClassName= descriptor.getDriverClass();
					try {
						Class<?> class1= _repository.loadClass(driverClassName);
						Driver driver2= (Driver)class1.newInstance();
						DriverManager.registerDriver(driver2);
					}
					catch (Throwable t) {
						Logging.warning("Failed to register JDBC driver:"+driverClassName, t);
					}
					
					try {
						driver= DriverManager.getDriver(url);
					}
					catch (Throwable t) {	
					}
				}
			}
		}
		
		if (driver == null)
			throw new StorageException("No suitable driver");
		
		return driver;
	}
	
	@Override public Set<JDBCDriverDescriptor> findAllDriverDescriptors() {
		return _repository.findResourcesByType(JDBCDriverDescriptor.class);
	}
	
}
