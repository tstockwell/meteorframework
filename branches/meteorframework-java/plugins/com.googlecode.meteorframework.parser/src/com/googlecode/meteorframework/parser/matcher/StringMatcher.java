/**
 * 
 */
package com.googlecode.meteorframework.parser.matcher;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public final class StringMatcher extends Matcher
{
	private final String string;

	public StringMatcher(String string)
	{
		this.string = string;
	}

	@Override
	public MatchResults match(final String input, int start) 
	{
		int i= 0;
		int l= string.length();
		int e= input.length();
		while (i < l && (start+i) < e && input.charAt(start + i) == string.charAt(i))
			i++;
		if (i == l)
			return new MatchResults(Arrays.<AbstractNode>asList(new StringNode(start, start + l, string)));
		return new MatchResults("Expected '"+string+"'", start + i);
	}
	
	@Override
	public String getLabel()
	{
		return "'"+string+"'";
	}
}