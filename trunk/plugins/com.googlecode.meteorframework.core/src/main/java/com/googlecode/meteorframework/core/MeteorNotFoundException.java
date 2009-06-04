package com.googlecode.meteorframework.core;


public class MeteorNotFoundException extends MeteorException {
	private static final long serialVersionUID = 1L;
	
	public MeteorNotFoundException(String uri) {
		super("Object not found:"+uri);
	}
}
