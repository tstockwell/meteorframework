package net.sf.plugins.library.derby.core;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DerbyActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
			// Load the derby JDBC driver
			new EmbeddedDriver();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// do nothing
	}

}
