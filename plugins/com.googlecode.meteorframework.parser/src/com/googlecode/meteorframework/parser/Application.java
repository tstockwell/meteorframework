package com.googlecode.meteorframework.parser;

import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.Matchers;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.NamedNode;
import com.googlecode.meteorframework.parser.node.Nodes;

public class Application
{
	public static void main(final String[] args) throws Exception
	{
		StringBuilder builder = new StringBuilder() //
				.append("positive-number ::= (%digit% { %digit% })").append("\n") //
				.append("negative-number ::= (\"-\" positive-number)").append("\n") //
				.append("addition ::= ((\"+\" | \"-\") (positive-number | (\"(\" negative-number \")\") | mul-expression))").append("\n") //
				.append("sum-expression ::= ((positive-number | negative-number | mul-expression) addition {addition})").append("\n") //
				.append("multiplication ::= ((\"*\" | \"/\") (positive-number | (\"(\" negative-number \")\") | (\"(\" sum-expression \")\")))").append("\n") //
				.append("mul-expression ::= ((positive-number | negative-number | (\"(\" sum-expression \")\")) multiplication {multiplication})").append("\n") //
				.append("expression ::= sum-expression");

		long start = System.currentTimeMillis();
		Matcher matcher = Grammar.generate(builder.toString(), "expression");
		long delay = System.currentTimeMillis() - start;
		System.out.println("Grammar generated in: " + delay);

		start = System.currentTimeMillis();
		AbstractNode node= Matchers.fullMatch(matcher, "2+2").matches.get(0);
		delay = System.currentTimeMillis() - start;
		System.out.println("Input parsed in: " + delay);

		System.out.println(Nodes.toStringNamed((NamedNode) node));
	}
}
