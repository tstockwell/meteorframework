package com.googlecode.meteorframework.utils;

import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.IsMethod;
import com.googlecode.meteorframework.annotation.Model;


/**
 * A marker interface for classes that provide messages and labels.
 * Interfaces that provide such text info should extend this interface, 
 * it makes it easy to find all the messages and labels in a system.
 * 
 * Eventually any message or label in a system should be obtained by 
 * eventually calling the getText method.
 * This gives other plugins a change to customize text by decorating 
 * the getText method.
 * 
 * The @ForValues binding annotation may be used to customize 
 * the getText method for a particular message. 
 * 
 * @author Ted Stockwell
 */
@Model public interface Messages extends Service {
	
	@IsMethod String getText(String defaultValue);
}
