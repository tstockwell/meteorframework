package com.googlecode.meteorframework.parser.node;

import java.util.List;

public class NamedNode extends AbstractNode
{

	public NamedNode(String id, int start, int end)
	{
		super(id, start, end);
	}

	public NamedNode(String id, int start, int end, String text)
	{
		super(id, start, end, text);
	}

	public List<NamedNode> getNamedChildren()
	{
		return Navigation.getNamedChildren(this);
	}

}