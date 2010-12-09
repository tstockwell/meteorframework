package com.googlecode.meteorframework.parser.exception;


import java.util.List;

import com.googlecode.meteorframework.parser.matcher.Matchers;
import com.googlecode.meteorframework.parser.node.AbstractNode;

public class AmbiguousGrammarException extends GrammarParsingException
{
	private static final long	serialVersionUID	= 1L;

	public AmbiguousGrammarException(List<AbstractNode> ambiguousResults)
	{
		super("May be parsed as [" + ambiguousResults.size() + "] trees:\n" + Matchers.toStringNamed(ambiguousResults));
	}
}
