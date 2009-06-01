package com.googlecode.meteorframework.parser.node;

public final class ChoiceNode extends PrimitiveNode
{
	private final int choiceIndex;
	
	public ChoiceNode(int choiceIndex, int start, int end)
	{
		super("#CHOICE[" + choiceIndex + "]", start, end);
		this.choiceIndex = choiceIndex;
	}
	
	public int getChoiceIndex()
	{
		return choiceIndex;
	}
}
