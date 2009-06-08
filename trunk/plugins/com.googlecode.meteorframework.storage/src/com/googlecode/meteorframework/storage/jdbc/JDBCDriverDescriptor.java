package com.googlecode.meteorframework.storage.jdbc;

import com.googlecode.meteorframework.core.annotation.Model;

@Model public interface JDBCDriverDescriptor {
	/**
	 * @return The JDBC protocol prefix used by the driver.
	 */
	public String getProtocol();
	
	/**
	 * @return The JDBC driver class.
	 */
	public String getDriverClass();
}
