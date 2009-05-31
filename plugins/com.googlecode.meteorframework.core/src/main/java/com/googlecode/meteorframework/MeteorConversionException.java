package com.googlecode.meteorframework;

import java.util.logging.Level;

import com.googlecode.meteorframework.utils.Logging;

public class MeteorConversionException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MeteorConversionException() {
	}

	public MeteorConversionException(String arg0) {
		super(arg0);
	}

	public MeteorConversionException(Throwable arg0) {
		super(arg0);
	}

	public MeteorConversionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	public static MeteorConversionException getMeteorException(String msg, Throwable cause) {
		MeteorConversionException exception= null;
		Throwable t= cause;
		while (t != null) {
			if (t instanceof MeteorConversionException)
				exception= (MeteorConversionException)t;
			t= t.getCause();
		}
		if (exception == null)
			exception= new MeteorConversionException(msg, cause);
		Logging.getLogger().log(Level.INFO, "", exception);
		return exception;
	}

}
