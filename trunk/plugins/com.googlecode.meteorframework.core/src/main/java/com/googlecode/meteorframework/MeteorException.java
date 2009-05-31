package com.googlecode.meteorframework;

import java.util.logging.Level;

import com.googlecode.meteorframework.utils.Logging;

public class MeteorException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MeteorException() {
	}

	public MeteorException(String arg0) {
		super(arg0);
	}

	public MeteorException(Throwable arg0) {
		super(arg0);
	}

	public MeteorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	public static MeteorException getMeteorException(String msg, Throwable cause) {
		MeteorException exception= null;
		Throwable t= cause;
		while (t != null) {
			if (t instanceof MeteorException)
				exception= (MeteorException)t;
			t= t.getCause();
		}
		if (exception == null) {
			exception= new MeteorException(msg, cause);
		}
		else if (msg != null)
			exception= new MeteorException(msg, cause);
		Logging.getLogger().log(Level.INFO, "", exception);
		return exception;
	}

}
