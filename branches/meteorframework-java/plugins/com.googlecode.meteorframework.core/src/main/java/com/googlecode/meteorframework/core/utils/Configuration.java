package com.googlecode.meteorframework.core.utils;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;


/**
 * A marker interface for classes that provide configuration information.
 * Interfaces that provide configuration info should extend this interface, 
 * it makes it easy to find all the configuration information in a system.
 * 
 * @author Ted Stockwell
 */
@ModelElement public interface Configuration extends Service {
}
