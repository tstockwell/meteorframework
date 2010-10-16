package com.googlecode.meteorframework.storage.appengine;

import com.googlecode.meteorframework.storage.StorageException;

public class AppengineStorageException
extends StorageException
{
	private static final long serialVersionUID = 1L;

	public AppengineStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppengineStorageException(String message) {
		super(message);
	}

	public AppengineStorageException(Throwable cause) {
		super(cause);
	}

}
