package com.googlecode.meteorframework.storage;

import java.util.Date;

import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * Represents a set of changes to a persistence store. 
 * @author Ted Stockwell
 */
@ModelElement
public interface Revision {
	Date getWhen();
	String getWho();
	long getOrder(); // I'll be way dead before this overflows!
}
