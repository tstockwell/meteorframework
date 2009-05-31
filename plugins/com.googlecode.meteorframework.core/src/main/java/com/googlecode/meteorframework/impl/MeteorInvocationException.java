package com.googlecode.meteorframework.impl;

import java.util.logging.Level;


import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.utils.Logging;

public class MeteorInvocationException extends MeteorException {
	private static final long serialVersionUID = 1L;
	
	public MeteorInvocationException() {
	}

	public MeteorInvocationException(String arg0) {
		super(arg0);
	}

	public MeteorInvocationException(Throwable arg0) {
		super(arg0);
	}

	public MeteorInvocationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	public static MeteorException getMeteorException(String msg, Throwable cause) {
		MeteorException exception= null;
		Throwable t= cause;
		while (t != null && exception == null) {
			if (t instanceof MeteorException)
				exception= (MeteorException)t;
			t= t.getCause();
		}
		if (exception == null)
			exception= new MeteorInvocationException(msg, cause);
		Logging.getLogger().log(Level.FINE, "", exception);
		return exception;
	}

}
