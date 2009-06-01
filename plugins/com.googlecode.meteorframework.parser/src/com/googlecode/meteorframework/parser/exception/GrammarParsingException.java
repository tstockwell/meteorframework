package com.googlecode.meteorframework.parser.exception;

public abstract class GrammarParsingException extends Exception
{
	private static final long serialVersionUID = 1L;

	public GrammarParsingException(String message)
	{
		super(message);
	}
}
