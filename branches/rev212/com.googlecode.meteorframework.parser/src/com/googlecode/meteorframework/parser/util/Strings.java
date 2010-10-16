package com.googlecode.meteorframework.parser.util;

public final class Strings
{
	private Strings()
	{
		throw new UnsupportedOperationException("Utility class");
	}

	public static String indent(String input)
	{
		return "\t" + input.replaceAll("\n", "\n\t");
	}
}
