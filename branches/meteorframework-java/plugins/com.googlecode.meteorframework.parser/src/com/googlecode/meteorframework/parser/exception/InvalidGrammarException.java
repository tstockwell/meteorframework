package com.googlecode.meteorframework.parser.exception;


import java.util.List;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matchers;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.NamedNode;

public class InvalidGrammarException extends GrammarParsingException
{
	private static final long serialVersionUID = 1L;
	public final MatchResults results;
	
	public InvalidGrammarException(MatchResults results)
	{
		super("Grammar cannot be fully parsed. Longest tree:\n" + Matchers.toStringNamed((NamedNode)getLongestResult(results.matches)));
		this.results= results;
	}

	private static AbstractNode getLongestResult(List<AbstractNode> results)
	{
		AbstractNode longestResult = results.get(0);

		for (AbstractNode result : results)
			if (result.end > longestResult.end) longestResult = result;
		
		return longestResult;
	}
}
