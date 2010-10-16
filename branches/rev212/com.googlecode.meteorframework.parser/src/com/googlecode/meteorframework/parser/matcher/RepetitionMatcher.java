/**
 * 
 */
package com.googlecode.meteorframework.parser.matcher;


import java.util.ArrayList;
import java.util.List;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.Parsers;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.RepetitionNode;

public final class RepetitionMatcher extends Matcher
{
	public enum Type
	{
		SEQUENCE, CHOICE
	}

	private final int min;
	private final int max;
	private final Matcher matcher;

	public RepetitionMatcher(int min, int max, Type type, Matcher... matchers)
	{
		this.min = min;
		this.max = max;

		switch (type)
		{
			case SEQUENCE:
				this.matcher = Parsers.seq(matchers);
				break;
			case CHOICE:
				this.matcher = Parsers.cho(matchers);
				break;
			default:
				throw new RuntimeException("Unknown repetition matcher subtype: " + type);
		}
	}

	public RepetitionMatcher(int min, int max, Matcher matcher)
	{
		this.min = min;
		this.max = max;
		this.matcher = matcher;
	}

	@Override
	public MatchResults match(final String input, int start) 
	{
		final List<AbstractNode> combinedResults = new ArrayList<AbstractNode>();

		final List<Matcher> repetition = new ArrayList<Matcher>();

		for (int i = 0; i < min; i++)
		{
			repetition.add(matcher);
		}

		MatchResults results= Parsers.seq(repetition.toArray(new Matcher[] {})).match(input, start);
		if (!results.success())
			return results;

		for (final AbstractNode result : results.matches)
		{
			final AbstractNode node = new RepetitionNode(start, result.end);
			node.children.addAll(result.children);
			combinedResults.add(node);
		}

		for (int i = 1; i <= max - min; i++)
		{
			final List<AbstractNode> combinedSubResults = new ArrayList<AbstractNode>();

			for (final AbstractNode result : results.matches)
			{
				final MatchResults subResults = Parsers.reps(i, i, matcher).match(input, result.end);

				for (final AbstractNode subResult : subResults.matches)
				{
					final AbstractNode node = new RepetitionNode(start, subResult.end);
					node.children.addAll(result.children);
					node.children.addAll(subResult.children);
					combinedSubResults.add(node);
				}
			}

			if (combinedSubResults.isEmpty())
			{
				break;
			}
			else
			{
				combinedResults.addAll(combinedSubResults);
			}
		}

		return new MatchResults(combinedResults);
	}
	
	@Override
	public String getLabel()
	{
		String label= "";
		if (0 < min)
			label+= "at least "+min+" repetitions";
		if (max < Integer.MAX_VALUE)
		{
			if (0 < label.length())
				label+=" but ";
			label+= "no more than "+max+" repetitions";
		}
		if (0 < label.length())
			label+= " of ";
		label+= matcher.getLabel();
		return label;
	}
}