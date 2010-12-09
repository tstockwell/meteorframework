package com.googlecode.meteorframework.parser.node;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode
{
	public final String id;	
	public final List<AbstractNode> children = new ArrayList<AbstractNode>();
	
	/**
	 * A String representation of the associated grammar element.
	 * This text is not necessarily the same as the contents of the parsed docment.   
	 */
	protected final String text; 

	/**
	 * The beginning position within the parsed document of the grammar element 
	 * represented by this node
	 */
	public int start;

	/**
	 * The ending position, plus one, of the grammar element represented by this node in the 
	 * original parsed document.
	 */
	public int end;
	
	public AbstractNode(final String id, final int start, final int end, String text)
	{
		assert start <= end;
		this.id = id;
		this.start= start;
		this.end= end;
		this.text= text;
	}
	
	public AbstractNode(final String id, final int start, final int end)
	{
		this(id, start, end, null);
	}
	
	public int length() {
		return end - start;
	}

	public String getText()
	{
		if (this.text != null) return this.text;

		final StringBuilder builder = new StringBuilder();

		for (final AbstractNode node : this.children)
		{
			builder.append(node.getText());
		}

		return builder.toString();
	}
	
}
