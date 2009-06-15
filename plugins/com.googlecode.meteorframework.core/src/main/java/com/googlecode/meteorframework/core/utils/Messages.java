package com.googlecode.meteorframework.core.utils;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * A marker interface for classes that provide messages and labels.
 * Interfaces that provide such text info should extend this interface, 
 * it makes it easy to find all the messages and labels in a system.
 * 
 * @author Ted Stockwell
 */
@Model public interface Messages extends Service {
	
}
