package com.googlecode.meteorframework.parser.node;

public abstract class PrimitiveNode extends AbstractNode
{
	public PrimitiveNode(String id, int position, int end)
	{
		super(id, position, end);
	}

	public PrimitiveNode(String id, int start, int end, String text)
	{
		super(id, start, end, text);
	}
}
