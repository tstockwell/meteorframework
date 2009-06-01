package com.googlecode.meteorframework.parser.matcher.common;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public class IntegerMatcher
extends Matcher
{
	@Override
	public MatchResults match(String input, int start) 
	{
		int i= start;
		if (start < input.length())
		{
			char c= input.charAt(start);
			int f= (c == '+' || c == '-') ? 1 : 0;
			
			if (Character.isDigit(input.charAt(f)))
			{
				i+= f + 1;
				while (Character.isDigit(input.charAt(i)))
					i++;
			}
		}
		
		if (i == start)
			return new MatchResults("Expected an integer", start);
		
		
		StringNode node= new StringNode(start, i, input.substring(0, i));
		return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "integer";
	}
}
