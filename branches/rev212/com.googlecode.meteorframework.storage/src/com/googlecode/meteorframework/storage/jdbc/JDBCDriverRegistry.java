package com.googlecode.meteorframework.storage.jdbc;

import java.sql.Driver;
import java.util.Set;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;

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
@ModelElement public interface JDBCDriverRegistry extends Service {
	
	/**
	 * Returns null if no supporting driver could be found.
	 */
	public Driver findDriver(String url);
	
	public Set<JDBCDriverDescriptor> findAllDriverDescriptors();
	
}
