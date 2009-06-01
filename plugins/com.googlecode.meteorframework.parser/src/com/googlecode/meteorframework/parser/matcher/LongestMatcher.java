/**
 * 
 */
package com.googlecode.meteorframework.parser.matcher;


import java.util.ArrayList;
import java.util.List;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.node.AbstractNode;

public class LongestMatcher extends ChoiceMatcher
{
	public LongestMatcher(Matcher... matchers)
	{
		super(matchers);
	}

	@Override
	public MatchResults match(final String input, int start) 
	{
		final List<AbstractNode> allResults = new ArrayList<AbstractNode>();
		int max= 0;
		
		for (int i = 0; i < matchers.length; i++)
		{
			MatchResults matchResults= matchers[i].match(input, start);
			if (!matchResults.success())
				return matchResults;
			for (AbstractNode match : matchers[i].match(input, start).matches)
			{
				allResults.add(match);
				if (max < match.end)
					max= match.end;
			}
		}

		if (allResults.isEmpty())
			return new MatchResults(super.getLabel(), start);
		
		final List<AbstractNode> longestResults = new ArrayList<AbstractNode>();
		for (AbstractNode result : allResults)
			if (max <= result.end)
				longestResults.add(result);

		return new MatchResults(longestResults);
	}

	public String getLabel()
	{
		String label= "the longest element that matches any of the following: ";
		for (int i= 0; i < matchers.length; i++)
		{
			if (0 < i)
				label+= (i == matchers.length - 1) ? ", or " : ", ";
			label+= matchers[i].getLabel();
		}
		return label;
	}
}