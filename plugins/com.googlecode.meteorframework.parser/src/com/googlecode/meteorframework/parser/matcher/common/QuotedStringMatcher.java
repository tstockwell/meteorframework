package com.googlecode.meteorframework.parser.matcher.common;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public class QuotedStringMatcher
extends Matcher
{
	@Override
	public MatchResults match(String input, int start) 
	{
		if (input.length() <= start || input.charAt(start) != '"' )
			return new MatchResults("Excepted beginning of quote", start);
		
		int pos = input.indexOf('"', start+1);
		while (pos > 0 && input.charAt(pos - 1) == '\'')
			pos = input.indexOf('"', pos+1);

		if (pos == -1 || input.charAt(pos) != '"') 
			return new MatchResults("Excepted end of quote", input.length());
		pos++;

		String text= input.substring(start, pos).replaceAll("\'\"", "\"");
		StringNode node= new StringNode(start, pos, text);
		return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "quoted string";
	}

}
