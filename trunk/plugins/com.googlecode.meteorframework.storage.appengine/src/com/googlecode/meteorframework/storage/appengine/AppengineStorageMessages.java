package com.googlecode.meteorframework.storage.appengine;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.annotation.Metadata;
import com.googlecode.meteorframework.core.annotation.Prefixes;
import com.googlecode.meteorframework.core.binding.BindingNS;
import com.googlecode.meteorframework.core.utils.Messages;

@Prefixes({
	"bindings", CoreNS.ModelElement.bindings,
	"formatted", BindingNS.Formatted.TYPE,
	"format", BindingNS.Formatted.format
})
public interface AppengineStorageMessages
extends Messages
{
	@Metadata("bindings: [a formatted:; format: 'No such root entity:{0}']")
	String getNoSuchRootEntity(String entityName);

}


