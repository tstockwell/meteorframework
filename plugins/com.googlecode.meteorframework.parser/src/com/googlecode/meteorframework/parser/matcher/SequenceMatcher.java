/**
 * 
 */
package com.googlecode.meteorframework.parser.matcher;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.Parsers;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.SequenceNode;

public final class SequenceMatcher extends Matcher
{
	private final Matcher[] matchers;

	public SequenceMatcher(Matcher[] matchers)
	{
		this.matchers = matchers;
	}

	@Override
	public MatchResults match(final String input, int start) 
	{
		if (matchers.length == 0) 
			return new MatchResults(Arrays.<AbstractNode>asList(new SequenceNode(start, start)));
		
		final List<AbstractNode> combinedResults = new ArrayList<AbstractNode>();

		MatchResults results= matchers[0].match(input, start);
		if (!results.success())
			return results;

		MatchResults err= null;
		for (final AbstractNode result : results.matches)
		{
			MatchResults subResults= Parsers.seq(Parsers.tail(matchers)).match(input, result.end);
			if (!subResults.success() && err == null)
				err= subResults;
			for (final AbstractNode subResult : subResults.matches)
			{
				final AbstractNode node = new SequenceNode(start, subResult.end);
				node.children.add(result);
				node.children.addAll(subResult.children);

				combinedResults.add(node);
			}
		}
		
		if (combinedResults.isEmpty() && err != null)
			return err;

		return new MatchResults(combinedResults);
	}

	@Override
	public String getLabel()
	{
		String label= "sequence of : ";
		for (int i= 0; i < matchers.length; i++)
		{
			if (0 < i)
				label+= ", ";
			label+= matchers[i].getLabel();
		}
		return label;
	}
}