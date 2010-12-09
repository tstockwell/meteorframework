package com.googlecode.meteorframework.parser.matcher;

import com.googlecode.meteorframework.parser.MatchResults;

public abstract class Matcher
{
	abstract public MatchResults match(String input, int start);
	
	public MatchResults match(String input) 
	{
		return match(input, 0);
	}
	
	abstract public String getLabel();
}