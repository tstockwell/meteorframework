package com.googlecode.meteorframework.parser;

import static com.googlecode.meteorframework.parser.Parsers.cho;
import static com.googlecode.meteorframework.parser.Parsers.lrepc;
import static com.googlecode.meteorframework.parser.Parsers.lreps;
import static com.googlecode.meteorframework.parser.Parsers.minc;
import static com.googlecode.meteorframework.parser.Parsers.mins;
import static com.googlecode.meteorframework.parser.Parsers.opt;
import static com.googlecode.meteorframework.parser.Parsers.optc;
import static com.googlecode.meteorframework.parser.Parsers.opts;
import static com.googlecode.meteorframework.parser.Parsers.range;
import static com.googlecode.meteorframework.parser.Parsers.rep;
import static com.googlecode.meteorframework.parser.Parsers.repc;
import static com.googlecode.meteorframework.parser.Parsers.reps;
import static com.googlecode.meteorframework.parser.Parsers.seq;
import static com.googlecode.meteorframework.parser.Parsers.str;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.meteorframework.parser.exception.AmbiguousGrammarException;
import com.googlecode.meteorframework.parser.exception.GrammarParsingException;
import com.googlecode.meteorframework.parser.exception.InvalidGrammarException;
import com.googlecode.meteorframework.parser.exception.UndefinedSymbolException;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.Matchers;
import com.googlecode.meteorframework.parser.matcher.NamedMatcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.NamedNode;
import com.googlecode.meteorframework.parser.node.Navigation;
import com.googlecode.meteorframework.parser.node.Nodes;

public class Grammar
{
	public static final Matcher GRAMMAR;

	public static Matcher generate(final String grammarCode, final String rootDefinition) 
	throws GrammarParsingException
	{
		final MatchResults results = Matchers.fullMatch(GRAMMAR, grammarCode);

		if (results.matches.size() <= 0) throw new InvalidGrammarException(results);
		if (results.matches.size() > 1) throw new AmbiguousGrammarException(results.matches);

		final NamedNode grammar = (NamedNode) results.matches.get(0);
		final Map<String, NamedMatcher> definitions = new HashMap<String, NamedMatcher>();

		for (final NamedNode definition : grammar.getNamedChildren())
		{
			final String identifier = definition.getNamedChildren().get(0).getText();
			definitions.put(identifier, new NamedMatcher(identifier));
		}

		for (final NamedNode definition : grammar.getNamedChildren())
		{
			final String identifier = definition.getNamedChildren().get(0).getText();
			final NamedNode expression = definition.getNamedChildren().get(1);
			definitions.get(identifier).define(createMatcher(expression, definitions));
		}

		// check for undefined identifiers
		List<AbstractNode> identifierNodes= Navigation.findAllById(grammar, "identifier");
		for (AbstractNode identifierNode : identifierNodes) 
		{
			String identifier= identifierNode.getText();
			if (definitions.get(identifier) == null)
				throw new UndefinedSymbolException(identifier);
		}

		Matcher matcher= definitions.get(rootDefinition);
		if (matcher == null)
			throw new UndefinedSymbolException(rootDefinition);
		return matcher;
	}

	private static Matcher createMatcher(final NamedNode expression, final Map<String, NamedMatcher> definitions)
	{
		final List<Matcher> matchersList = new ArrayList<Matcher>();

		for (final NamedNode subExpression : expression.getNamedChildren())
		{
			matchersList.add(createMatcher(subExpression, definitions));
		}

		final Matcher[] matchers = matchersList.toArray(new Matcher[] {});

		if ("seq".equals(expression.id)) return seq(matchers);
		if ("cho".equals(expression.id)) return cho(matchers);

		if ("rep".equals(expression.id)) return rep(createMatcher(expression.getNamedChildren().get(0), definitions));
		if ("reps".equals(expression.id)) return reps(matchers);
		if ("repc".equals(expression.id)) return repc(matchers);

		if ("opt".equals(expression.id)) return opt(createMatcher(expression.getNamedChildren().get(0), definitions));
		if ("opts".equals(expression.id)) return opts(matchers);
		if ("optc".equals(expression.id)) return optc(matchers);

		if ("string".equals(expression.id)) return str(expression.getText());
		if ("identifier".equals(expression.id)) return definitions.get(expression.getText());
		if ("expression".equals(expression.id)) return createMatcher(expression.getNamedChildren().get(0), definitions);

		if ("anyLower".equals(expression.id)) return range('a', 'z');
		if ("anyUpper".equals(expression.id)) return range('A', 'Z');
		if ("anyAlpha".equals(expression.id)) return cho(range('a', 'z'), range('A', 'Z'));
		if ("anyDigit".equals(expression.id)) return range('0', '9');
		if ("anyWhitespace".equals(expression.id)) return minc(1, str(" "), str("\t"), str("\n"), str("\r"));

		throw new RuntimeException("Cannot parse expresion [" + expression.id + "]:\n" + Nodes.toStringFull(expression));
	}

	static
	{
		final Matcher whitespace = minc(1, str(" "), str("\t"), str("\n"), str("\r"));
		final Matcher optwh = opt(whitespace);

		final Matcher lower = range('a', 'z');
		final Matcher upper = range('A', 'Z');
		final Matcher alpha = cho(lower, upper);
		final Matcher digit = range('0', '9');

		final Matcher normalWord = seq(alpha, lrepc(digit, alpha));
		final NamedMatcher anyLower = new NamedMatcher("anyLower").define(str("%lower%"));
		final NamedMatcher anyUpper = new NamedMatcher("anyUpper").define(str("%upper%"));
		final NamedMatcher anyAlpha = new NamedMatcher("anyAlpha").define(str("%alpha%"));
		final NamedMatcher anyDigit = new NamedMatcher("anyDigit").define(str("%digit%"));
		final NamedMatcher anyWhitespace = new NamedMatcher("anyWhitespace").define(str("%whitespace%"));

		final NamedMatcher expression = new NamedMatcher("expression");
		final NamedMatcher definition = new NamedMatcher("definition");
		final NamedMatcher grammar = new NamedMatcher("grammar");
		final NamedMatcher identifier = new NamedMatcher("identifier");
		final NamedMatcher string = new NamedMatcher("string");

		final NamedMatcher seq = new NamedMatcher("seq").define( //
				seq(str("("), optwh, expression, mins(1, whitespace, expression), optwh, str(")")));

		final NamedMatcher cho = new NamedMatcher("cho").define( //
				seq(str("("), optwh, expression, mins(1, optwh, str("|"), optwh, expression), optwh, str(")")));

		final NamedMatcher rep = new NamedMatcher("rep").define( //
				seq(str("{"), optwh, expression, optwh, str("}")));

		final NamedMatcher reps = new NamedMatcher("reps").define( //
				seq(str("{"), optwh, expression, mins(1, whitespace, expression), optwh, str("}")));

		final NamedMatcher repc = new NamedMatcher("repc").define( //
				seq(str("{"), optwh, expression, mins(1, optwh, str("|"), optwh, expression), optwh, str("}")));

		final NamedMatcher opt = new NamedMatcher("opt").define( //
				seq(str("["), optwh, expression, optwh, str("]")));

		final NamedMatcher opts = new NamedMatcher("opts").define( //
				seq(str("["), optwh, expression, mins(1, whitespace, expression), optwh, str("]")));

		final NamedMatcher optc = new NamedMatcher("optc").define( //
				seq(str("["), optwh, expression, mins(1, optwh, str("|"), optwh, expression), optwh, str("]")));

		string.define(new Matcher()
		{
			@Override
			public MatchResults match(String input, int start)
			{
				int pos = input.indexOf('"', start);
				if (pos < 0)
					return new MatchResults("Expected beginning of quote", start);
				while (pos > 0 && input.charAt(pos - 1) == '\'')
					pos = input.indexOf('"', pos+1);

				if (pos == -1) 
					return new MatchResults("Expected end of quote started at position "+start, input.length());
				
				String text= input.substring(start, pos).replaceAll("\'\"", "\"");
				return new MatchResults(Arrays.<AbstractNode>asList(new NamedNode("string", start, pos, text)));
			}
			
			@Override
			public String getLabel()
			{
				return "quoted string";
			}
		});

		identifier.define(seq(normalWord, lreps(str("-"), normalWord)));
		definition.define(seq(identifier, optwh, str("::="), optwh, expression));

		expression.define(cho( //
				seq, rep, reps, repc, opt, opts, optc, cho, // 
				identifier, seq(str("\""), string, str("\"")), //
				anyLower, anyUpper, anyAlpha, anyDigit, anyWhitespace));

		grammar.define(seq(optwh, reps(definition, whitespace), opt(definition)));

		GRAMMAR = grammar;
	}
}
