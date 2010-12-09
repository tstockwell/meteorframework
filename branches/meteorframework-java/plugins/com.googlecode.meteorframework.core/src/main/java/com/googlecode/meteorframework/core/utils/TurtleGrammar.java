package com.googlecode.meteorframework.core.utils;

import static com.googlecode.meteorframework.parser.Parsers.cho;
import static com.googlecode.meteorframework.parser.Parsers.lcho;
import static com.googlecode.meteorframework.parser.Parsers.lmins;
import static com.googlecode.meteorframework.parser.Parsers.lreps;
import static com.googlecode.meteorframework.parser.Parsers.lseq;
import static com.googlecode.meteorframework.parser.Parsers.min;
import static com.googlecode.meteorframework.parser.Parsers.opt;
import static com.googlecode.meteorframework.parser.Parsers.opts;
import static com.googlecode.meteorframework.parser.Parsers.range;
import static com.googlecode.meteorframework.parser.Parsers.reps;
import static com.googlecode.meteorframework.parser.Parsers.seq;
import static com.googlecode.meteorframework.parser.Parsers.str;

import java.util.Arrays;
import java.util.HashMap;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.NameMatcher;
import com.googlecode.meteorframework.parser.matcher.NamedMatcher;
import com.googlecode.meteorframework.parser.matcher.common.BooleanMatcher;
import com.googlecode.meteorframework.parser.matcher.common.DecimalMatcher;
import com.googlecode.meteorframework.parser.matcher.common.DoubleMatcher;
import com.googlecode.meteorframework.parser.matcher.common.IntegerMatcher;
import com.googlecode.meteorframework.parser.matcher.common.QuotedStringMatcher;
import com.googlecode.meteorframework.parser.matcher.common.WhitespaceMatcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.StringNode;

public class TurtleGrammar
{
	
	static HashMap<String, Matcher> __matchersById= new HashMap<String, Matcher>(); 
	static final String UTF8= "UTF-8";
	
	/**
	 * Symbols used in this grammar
	 */
	public static final String DOCUMENT = "turtleDocument";
	public static final String STATEMENT = "statement";
	public static final String DIRECTIVE = "directive";
	public static final String TRIPLES = "triples";
	public static final String PREDICATE_VALUES = "predicateValues";
	public static final String VERB = "verb";
	public static final String OBJECT_LIST = "objectList";
	public static final String OBJECT = "object";
	public static final String LITERAL = "literal";
	public static final String LANGUAGE = "language";
	public static final String DATATYPE_STRING = "datatypeString";
	public static final String INTEGER = "integer";
	public static final String DOUBLE = "double";
	public static final String DECIMAL = "decimal";
	public static final String BOOL = "bool";
	public static final String PREFIX_ID = "prefixID";
	public static final String BASE = "base";
	public static final String SUBJECT = "subject";
	public static final String RESOURCE = "resource";
	public static final String URIREF = "uriref";
	public static final String PREFIX_NAME = "prefixName";
	public static final String NODE_ID = "nodeID";
	public static final String QNAME = "qname";
	public static final String QUOTED_STRING = "quotedString";
	
	static {
		Matcher lower = range('a', 'z');
		Matcher digit = range('0', '9');
		Matcher ws = new WhitespaceMatcher();
		Matcher optws = opt(ws);
		Matcher name= new NameMatcher();
		
		NamedMatcher quotedString= defineMatcher(QUOTED_STRING, new QuotedStringMatcher());
		NamedMatcher qname= defineMatcher(QNAME, lseq(opt(name), str(":"), opt(name)));
		NamedMatcher nodeID= defineMatcher(NODE_ID, seq(str("_:"), name));
		NamedMatcher prefixName= defineMatcher(PREFIX_NAME, name);
		NamedMatcher uriref= defineMatcher(URIREF, new URIRefMatcher());
		NamedMatcher resource= defineMatcher(RESOURCE, cho(uriref, qname));
		NamedMatcher subject= defineMatcher(SUBJECT, cho(resource, nodeID, str("[]")));
		NamedMatcher base= defineMatcher(BASE, seq(str("@base"), ws, uriref));
		NamedMatcher prefixID= defineMatcher(PREFIX_ID, seq(str("@prefix"), ws, opt(prefixName), str(":"), ws, uriref));
		NamedMatcher bool= defineMatcher(BOOL, new BooleanMatcher());
		NamedMatcher decimal= defineMatcher(DECIMAL, new DecimalMatcher());
		NamedMatcher doubl= defineMatcher(DOUBLE, new DoubleMatcher());
		NamedMatcher integer= defineMatcher(INTEGER, new IntegerMatcher());
		NamedMatcher datatypeString= defineMatcher(DATATYPE_STRING, seq(quotedString, str("^^"), resource));
		NamedMatcher language= defineMatcher(LANGUAGE, seq(min(1, lower), reps(str("-"), min(1, seq(cho(lower, digit))))));
		NamedMatcher literal= defineMatcher(LITERAL, cho(seq(quotedString, opts(str("@"), language)), datatypeString, integer, doubl, decimal, bool));
		NamedMatcher object= defineMatcher(OBJECT, cho(resource, literal, nodeID, str("[]")));
		NamedMatcher objectList= defineMatcher(OBJECT_LIST, seq(object, lreps(optws, str(","), optws, object)));
		NamedMatcher verb= defineMatcher(VERB, lcho(resource, str("a")));
		NamedMatcher predicateValues= defineMatcher(PREDICATE_VALUES, seq(verb, ws, objectList));
		NamedMatcher triples= defineMatcher(TRIPLES, seq(subject, ws, predicateValues, lreps(optws, str(";"), optws, predicateValues)));
		NamedMatcher directive= defineMatcher(DIRECTIVE, cho(prefixID, base));
		NamedMatcher statement= defineMatcher(STATEMENT, seq(cho(directive, triples), optws, str(".")));
		defineMatcher(DOCUMENT, seq(optws, lmins(1, statement, optws)));
	}
	
	private static NamedMatcher defineMatcher(String symbol, Matcher definition) {
		NamedMatcher matcher= new NamedMatcher(symbol).define(definition);
		__matchersById.put(symbol, matcher);
		return matcher;
	}
	
	public static Matcher getMatcher(String symbol) {
		return __matchersById.get(symbol);
	}
	public static Matcher getMatcher() {
		return getMatcher(DOCUMENT);
	}
	

	public static class URIRefMatcher extends Matcher 
	{
		@Override 
		public MatchResults match(String input, int start)
		{
			if (input.length() <= start || input.charAt(start) != '<')
				return new MatchResults("Expected '<'", start);
			
			int pos = input.indexOf('>', start);
			if (pos < 0)
				return new MatchResults("Expected end of URI reference", start);
			
			pos++;
			StringNode node= new StringNode(start, pos, input.substring(start, pos));
			return new MatchResults(Arrays.<AbstractNode>asList(node));
		}
		
		@Override
		public String getLabel()
		{
			return "URI reference";
		}
	}
	
}
