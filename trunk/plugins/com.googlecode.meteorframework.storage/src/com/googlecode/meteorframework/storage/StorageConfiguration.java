package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.utils.Configuration;

@ModelElement public interface StorageConfiguration extends Configuration {

	@DefaultValue("meteor:storage:appengine")
	public String getDefaultConnectionURL();
}
