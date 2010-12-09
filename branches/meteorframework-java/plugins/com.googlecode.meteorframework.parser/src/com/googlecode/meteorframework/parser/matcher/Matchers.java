package com.googlecode.meteorframework.parser.matcher;


import java.util.ArrayList;
import java.util.List;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.NamedNode;
import com.googlecode.meteorframework.parser.node.Nodes;

public final class Matchers
{
	private Matchers()
	{
		throw new UnsupportedOperationException("Utility class");
	}

	public static MatchResults fullMatch(final Matcher matcher, final String input) 
	{
		final MatchResults partResults = matcher.match(input, 0);
		if (!partResults.success())
			return partResults;
		
		final List<AbstractNode> fullResults = new ArrayList<AbstractNode>();
		for (final AbstractNode match : partResults.matches)
		{
			if (match.length() == input.length())
			{
				fullResults.add(match);
			}
		}

		return new MatchResults(fullResults);
	}

	public static String toStringFull(AbstractNode node)
	{
		return "Matched [" + node.length() + "] symbols, tree:\n" + Nodes.toStringFull(node) + "\n";
	}

	public static String toStringNamed(NamedNode node)
	{
		return "Matched [" + node.length() + "] symbols, tree:\n" + Nodes.toStringNamed(node) + "\n";
	}

	public static String toStringFull(List<AbstractNode> results)
	{
		StringBuilder builder = new StringBuilder();

		for (AbstractNode result : results)
			builder.append(toStringFull(result));
		
		return builder.toString();
	}

	public static String toStringNamed(List<AbstractNode> results)
	{
		StringBuilder builder = new StringBuilder();

		for (AbstractNode result : results)
			builder.append(toStringNamed((NamedNode)result));
		
		return builder.toString();
	}
}
