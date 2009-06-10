package com.googlecode.meteorframework.storage;

public class UnsupportedStorageException 
extends StorageException 
{
	private static final long serialVersionUID = 1L;

	public UnsupportedStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedStorageException(String message) {
		super(message);
	}

}
