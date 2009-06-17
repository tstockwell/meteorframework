package com.googlecode.meteorframework.storage.appengine;

import com.googlecode.meteorframework.core.annotation.FormattedMessage;
import com.googlecode.meteorframework.core.utils.Messages;

public interface AppengineStorageMessages
extends Messages
{
	@FormattedMessage("No such root entity:{0}")
	String getNoSuchRootEntity(String entityName);

}
