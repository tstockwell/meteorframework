package com.googlecode.meteorframework.core.utils;

import java.text.Format;

/**
 * Adapter interface for formatted objects that provides access to the 
 * format of the object.
 * 
 * @author Ted Stockwell
 */
public interface FormattedObject {
	Format getFormat();
}
