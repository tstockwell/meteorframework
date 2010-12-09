package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.MeteorException;

public class StorageException extends MeteorException {
	private static final long serialVersionUID = 1L;

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
