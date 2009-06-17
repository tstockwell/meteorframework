package com.googlecode.meteorframework.core.impl.utils;

import java.text.MessageFormat;
import java.util.List;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.annotation.Binding;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.binding.Formatted;
import com.googlecode.meteorframework.core.utils.Messages;

/**
 * Implements formatted message properties.
 * @author Ted Stockwell
 */
@Decorator @Binding(Formatted.class)  
abstract public class MessagesImpl
implements Messages
{
	/**
	 * Override all message properties, like WebbenchMessages.signIn and 
	 * run the text through the Messages.getText method so that plugins 
	 * may customize. 
	 */
	public String getProperty(Property<String> property, Object...parameters)
	{
		BindingContext bindingContext= Meteor.getInvocationContext().getBindingContext();
		List<Formatted> allFormatted= bindingContext.findInstances(Formatted.class);
		for (Formatted formatted : allFormatted)
		{
			String pattern= formatted.getFormat();
			if (pattern != null)
				return MessageFormat.format(pattern, parameters);
		}
		return (String)Meteor.proceed();
	}
}
