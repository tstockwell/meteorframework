package com.googlecode.meteorframework.parser.matcher.common;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

/**
 * Matches both single quoted or double quoted strings
 * 
 * @author Ted Stockwell
 */
public class QuotedStringMatcher
extends Matcher
{
	@Override
	public MatchResults match(String input, int start) 
	{
		if (input.length() <= start)
			return new MatchResults("Unexpected end of input", start);
			
		char startChar= input.charAt(start);
		if (startChar != '"' && startChar != '\'')
			return new MatchResults("Excepted beginning of quote", start);
		
		char escapeChar= (startChar == '"') ? '\'' : '"';
		int pos = input.indexOf(startChar, start+1);
		while (pos > 0 && input.charAt(pos - 1) == escapeChar)
			pos = input.indexOf(startChar, pos+1);

		if (pos == -1 || input.charAt(pos) != startChar) 
			return new MatchResults("Excepted end of quote", input.length());
		pos++;

		String text= input.substring(start, pos).replaceAll(""+escapeChar+startChar, ""+startChar);
		StringNode node= new StringNode(start, pos, text);
		return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "quoted string";
	}

}
