package com.googlecode.meteorframework.parser.matcher.common;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public class BooleanMatcher
extends Matcher
{
	static final String T= "true";
	static final String F= "false";
	
	@Override
	public MatchResults match(String input, int start) 
	{
		String s= null;
		if (start < input.length())
		{
			String string= input.substring(start);
			if (string.startsWith(T)) {
				s= T;
			}
			else if (string.startsWith(F))
				s= F;
		}

		if (s == null) 
			return new MatchResults("Expected 'true' or 'false'", start);

		StringNode node= new StringNode(start, start+s.length(), s);
		return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "boolean";
	}
}
