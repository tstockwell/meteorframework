package com.googlecode.meteorframework.core.impl.utils;

import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.annotation.Lookup;
import com.googlecode.meteorframework.core.utils.Messages;

@Decorator abstract public class MessagesImpl
implements Messages, Resource
{
	@Inject Scope _scope;
	@Inject Messages _messages;
	
	@Lookup(MeteorNS.Resource.TYPE) Type<Resource> _resourceType;
	
	@Override
	public String getText(String defaultValue)
	{
		return defaultValue;
	}
	

	/**
	 * Override all message properties, like WebbenchMessages.signIn and 
	 * run the text through the Messages.getText method so that plugins 
	 * may customize. 
	 */
	public Object getProperty(String propertyURI)
	{
		Property<?> property= _scope.findResourceByURI(propertyURI, Property.class);
		if (property == null)
			Meteor.proceed();

		// if property belongs to Resource then ignore
		if (property.getDomain().contains(_resourceType))
			Meteor.proceed();
		
		Object result= Meteor.proceed();
		if (result instanceof String)
			result= _messages.getText((String)result); 
		
		return result;
	}
}
