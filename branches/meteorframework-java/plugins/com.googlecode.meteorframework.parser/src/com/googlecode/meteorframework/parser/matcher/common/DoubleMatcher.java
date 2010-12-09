package com.googlecode.meteorframework.parser.matcher.common;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public class DoubleMatcher
extends Matcher
{
	static final private IntegerMatcher __integerMatcher= new IntegerMatcher();
	//decimal ::= ([sign] ((digits "." {digits}) | ("." digits) | digits)) 
	@Override
	public MatchResults match(String input, int start) 
	{
		int i= start;
		MatchResults integers= __integerMatcher.match(input, i);
		if (integers.matches.isEmpty()) 
		{
			if ('.' == input.charAt(0))
			{
				integers= __integerMatcher.match(input, start + 1);
				if (!integers.matches.isEmpty()) 
					i= integers.matches.get(0).end;
			}
		}
		else 
		{
			i= integers.matches.get(0).end;
			if ('.' == input.charAt(i)) 
			{
				integers= __integerMatcher.match(input, ++i);
				if (!integers.matches.isEmpty()) 
					i= integers.matches.get(0).end;
			}
		}
		
		if (i == start)
			return new MatchResults("Expected a double", start);
		
		StringNode node= new StringNode(start, i, input.substring(start, i));
		return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "double";
	}
}
