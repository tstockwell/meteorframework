package com.googlecode.meteorframework;


public class MeteorMethodNotImplementedException extends MeteorException {
	private static final long serialVersionUID = 1L;
	
	private String _methodURI;
	
	public MeteorMethodNotImplementedException(String methodURI) {
		super("Method not implemented: "+methodURI);
	}
	
	public String getMethodURI() {
		return _methodURI;		
	}
}
