package com.googlecode.meteorframework.parser.matcher;

import java.util.Arrays;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

/**
 * Matches strings that begin with any character accepted by the isNameStartChar 
 * method and followed by any number of characters accepted by the isNameChar method.
 * 
 * @author Ted Stockwell
 */
public class NameMatcher extends Matcher {
	@Override 
	public MatchResults match(String input, int start)
	{
			char[] cs= input.toCharArray();
			if (cs.length <= start)
				return new MatchResults("A name must have at least one letter", start);
			if (!isNameStartChar(cs[start]))
				return new MatchResults("Not a valid name start charaacter", start);

			int i= start+1;
			for (; i < cs.length; i++) {
				if (!isNameChar(cs[i]))
					break;
			}
			StringNode node= new StringNode(start, i, input.substring(start, i));
			return new MatchResults(Arrays.<AbstractNode>asList(node));
	}
	
	@Override
	public String getLabel()
	{
		return "name";
	}
	
	public boolean isNameStartChar(char c) {

		if ('A' <= c && c <= 'Z')
			return true;

		if ('a' <= c && c <= 'z')
			return true;

		if (c == '_')
			return true;

		if (0x00C0 <= c && c <= 0x00D6)
			return true;

		if (0x00D8 <= c && c <= 0x00F6)
			return true;

		if (0x00F8 <= c && c <= 0x02FF)
			return true;

		if (0x0370 <= c && c <= 0x037D)
			return true;

		if (0x037F <= c && c <= 0x1FFF)
			return true;
		
		if (0x200C <= c && c <= 0x200D)
			return true;

		if (0x2070 <= c && c <= 0x218F)
			return true;

		if (0x2C00 <= c && c <= 0x2FEF)
			return true;

		if (0x3001 <= c && c <= 0xD7FF)
			return true;

		if (0xF900 <= c && c <= 0xFDCF)
			return true;

		if (0xFDF0 <= c && c <= 0xFFFD)
			return true;

		if (0x10000 <= c && c <= 0xEFFFF)
			return true;
		
		return false;

	}
	
	public boolean isNameChar(char c) 
	{
		if (isNameStartChar(c))
			return true;

		if (0xFDF0 <= c && c <= 0xFFFD)
			return true;

		if ('0' <= c && c <= '9')
			return true;

		if ('-' == c)
			return true;

		if (0x00B7 == c)
			return true;

		if (0x0300 <= c && c <= 0x036F)
			return true;

		if (0x203F <= c && c <= 0x2040)
			return true;
		
		return false;
	}

}
