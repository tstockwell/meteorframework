package com.googlecode.meteorframework.parser;

import static com.googlecode.meteorframework.parser.Parsers.cho;
import static com.googlecode.meteorframework.parser.Parsers.opt;
import static com.googlecode.meteorframework.parser.Parsers.reps;
import static com.googlecode.meteorframework.parser.Parsers.seq;
import static com.googlecode.meteorframework.parser.Parsers.str;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.meteorframework.parser.Grammar;
import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.exception.GrammarParsingException;
import com.googlecode.meteorframework.parser.matcher.Matcher;

public class ParserTest
{
	@Test
	public void testValSuccess() throws ParseException
	{
		Matcher val = str("test");
		MatchResults results = val.match("test1");

		Assert.assertTrue(results.success());
		Assert.assertEquals(1, results.matches.size());
		Assert.assertEquals(4, results.matches.get(0).end);
		Assert.assertTrue(results.matches.get(0).children.isEmpty());
	}

	@Test
	public void testValFailure() throws ParseException
	{
		Matcher val = str("test");
		MatchResults matchResults= val.match("tes1t");

		Assert.assertFalse(matchResults.success());
		Assert.assertEquals(3, matchResults.position);
	}

	@Test
	public void testSeqSuccess() throws ParseException
	{
		Matcher seq = seq(str("1"), str("2"));
		MatchResults results = seq.match("123");

		Assert.assertTrue(results.success());
		Assert.assertEquals(1, results.matches.size());
		Assert.assertEquals(2, results.matches.get(0).end);
		Assert.assertEquals(2, results.matches.get(0).children.size());
	}

	@Test
	public void testSeqFailure() throws ParseException
	{
		Matcher seq = seq(str("1"), str("2"));
		MatchResults results = seq.match("132");
		
		Assert.assertFalse(results.success());
		Assert.assertEquals(1, results.position);
	}

	@Test
	public void testChoSuccess() throws ParseException
	{
		Matcher cho = cho(str("12"), str("123"));
		MatchResults results = cho.match("1234");

		Assert.assertTrue(results.success());
		Assert.assertEquals(2, results.matches.size());
		Assert.assertEquals(2, results.matches.get(0).end);
		Assert.assertEquals(3, results.matches.get(1).end);
		Assert.assertTrue(!results.matches.get(0).children.isEmpty());
		Assert.assertTrue(!results.matches.get(1).children.isEmpty());
	}

	@Test
	public void testChoFailure() throws ParseException
	{
		Matcher cho = cho(str("12"), str("123"));
		MatchResults results = cho.match("132");

		Assert.assertFalse(results.success());
		Assert.assertEquals(0, results.position);
	}

	@Test
	public void testOptPresent() throws ParseException
	{
		Matcher opt = opt(str("12"));
		MatchResults results = opt.match("123");

		Assert.assertTrue(results.success());
		Assert.assertEquals(2, results.matches.size());
	}

	@Test
	public void testOptAbsent() throws ParseException
	{
		Matcher opt = opt(str("12"));
		MatchResults results = opt.match("132");

		Assert.assertTrue(results.success());
		Assert.assertEquals(1, results.matches.size());
	}

	@Test
	public void testRepSuccess1() throws ParseException
	{
		Matcher rep = reps(1, 3, str("12"));
		MatchResults results = rep.match("121212121");

		Assert.assertTrue(results.success());
		Assert.assertEquals(3, results.matches.size());
		Assert.assertEquals(2, results.matches.get(0).end);
		Assert.assertEquals(4, results.matches.get(1).end);
		Assert.assertEquals(6, results.matches.get(2).end);
	}

	@Test
	public void testRepSuccess2() throws ParseException
	{
		Matcher rep = reps(str("12"));
		MatchResults results = rep.match("1212121");

		Assert.assertTrue(results.success());
		Assert.assertEquals(4, results.matches.size());
		Assert.assertEquals(0, results.matches.get(0).end);
		Assert.assertEquals(2, results.matches.get(1).end);
		Assert.assertEquals(4, results.matches.get(2).end);
		Assert.assertEquals(6, results.matches.get(3).end);
	}

	@Test
	public void testRepFailure() throws ParseException
	{
		Matcher rep = reps(2, 3, str("123"));
		MatchResults results = rep.match("12312123");

		Assert.assertFalse(results.success());
		Assert.assertTrue(results.matches.isEmpty());
		Assert.assertEquals(5, results.position);
	}

	@Test
	public void testDetectMissingSymbols()
	{
		String grammar= "a ::= (b c)\nb ::= %digit%";
		try {
			Grammar.generate(grammar, "a");
			Assert.fail("The Grammar.generate method failed to detect that the 'c' symbol is not defined.");
		}
		catch (GrammarParsingException e) {
		}
	}
}
