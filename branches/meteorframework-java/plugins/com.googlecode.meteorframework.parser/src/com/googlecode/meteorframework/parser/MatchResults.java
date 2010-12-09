package com.googlecode.meteorframework.parser;


import java.util.Collections;
import java.util.List;

import com.googlecode.meteorframework.parser.node.AbstractNode;

public class MatchResults
{
	/**
	 * A message that describes the parsing failure.
	 */
	public final String errorMsg;
	
	/**
	 * The position within the parsed document where matching failed.
	 */
	public final int position;
	
	public final List<AbstractNode> matches;
	
	private MatchResults(String errorMsg, int position, List<AbstractNode> matches) 
	{
		this.errorMsg= errorMsg;
		this.position= position;
		this.matches= matches;
	}
	
	public MatchResults(String errorMsg, int position) 
	{
		this(errorMsg, position, Collections.<AbstractNode>emptyList());
	}
	
	public MatchResults(List<AbstractNode> matches) 
	{
		this(null, -1, matches);
	}
	
	public boolean success() {
		return position < 0;
	}
}
