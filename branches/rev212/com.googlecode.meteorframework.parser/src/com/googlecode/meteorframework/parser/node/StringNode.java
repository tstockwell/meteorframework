package com.googlecode.meteorframework.parser.node;

public final class StringNode extends PrimitiveNode
{
	public StringNode(int start, int end, String text)
	{
		super("#STRING", start, end, text);
	}
	
}
