package net.sf.meteor.storage.triplestore;

import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.Description;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.utils.Configuration;

@Model public interface TripleStoreConfiguration extends Configuration {
	
	public static final String TRIPLESTORE_PROTOCOL = "meteor:storage:triplestore";

	@DefaultValue("jdbc:h2:mem:meteor")
	public String getDefaultJdbcURL();
	
	@Description("The maximum number of connections") 
	@DefaultValue("10")
	public int getMaxConnectionCount();
	
	@Description("The maximum time in seconds to wait for a free connection. " +
			"A value of 0 or less indicates to wait forever.") 
	@DefaultValue("60")
	public int getConnectionTimeout();
}
