package com.googlecode.meteorframework.storage.appengine;

import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.utils.Messages;

public interface AppengineStorageMessages
extends Messages
{
	@Formatted("No such root entity:{0}")
	String getNoSuchRootEntity(String entityName);

}
