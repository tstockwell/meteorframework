package com.googlecode.meteorframework.parser.matcher.common;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public class WhitespaceMatcher extends Matcher
{
	@Override
	public MatchResults match(String input, int start) 
	{
		int len= input.length();
		int i= start;
		while (i < len && Character.isWhitespace(input.charAt(i)))
			i++;
		if (i <= start)
			return new MatchResults("Expected whitespace", start);
		StringNode node= new StringNode(start, i, input.substring(start, i));
		return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "whitespace";
	}
}
