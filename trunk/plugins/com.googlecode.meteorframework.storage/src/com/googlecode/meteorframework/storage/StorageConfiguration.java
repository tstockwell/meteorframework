package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.utils.Configuration;

@Model public interface StorageConfiguration extends Configuration {

	@DefaultValue("meteor:storage:triplestore")
	public String getDefaultConnectionURL();
}
