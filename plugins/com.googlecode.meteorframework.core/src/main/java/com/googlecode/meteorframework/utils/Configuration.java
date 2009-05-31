package com.googlecode.meteorframework.utils;

import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Model;


/**
 * A marker interface for classes that provide configuration information.
 * Interfaces that provide configuration info should extend this interface, 
 * it makes it easy to find all the configuration information in a system.
 * 
 * @author Ted Stockwell
 */
@Model public interface Configuration extends Service {
}
