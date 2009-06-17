package com.googlecode.meteorframework.storage.appengine;

import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.utils.Configuration;

@ModelElement public interface AppengineStorageConfiguration extends Configuration {
	
	public static final String APPENGINE_STORAGE_PROTOCOL = "meteor:storage:appengine";

}
