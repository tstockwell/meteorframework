package com.googlecode.meteorframework.storage.appengine;


public class RootEntityNotFound
extends AppengineStorageException
{
	private static final long serialVersionUID = 1L;

	public RootEntityNotFound(String message, Throwable cause) {
		super(message, cause);
	}

	public RootEntityNotFound(String message) {
		super(message);
	}

	public RootEntityNotFound(Throwable cause) {
		super(cause);
	}
}
