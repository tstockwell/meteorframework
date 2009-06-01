package com.googlecode.meteorframework.parser;


import java.util.Arrays;

import com.googlecode.meteorframework.parser.matcher.ChoiceMatcher;
import com.googlecode.meteorframework.parser.matcher.ExcludingMatcher;
import com.googlecode.meteorframework.parser.matcher.LongestMatcher;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.RangeMatcher;
import com.googlecode.meteorframework.parser.matcher.RepetitionMatcher;
import com.googlecode.meteorframework.parser.matcher.SequenceMatcher;
import com.googlecode.meteorframework.parser.matcher.StringMatcher;

public final class Parsers
{
	private Parsers()
	{
		throw new UnsupportedOperationException("Utility class");
	}

	// ============================================================================================
	// === BASIC MATCHERS
	// ============================================================================================

	public static Matcher str(final String string)
	{
		return new StringMatcher(string);
	}

	public static Matcher range(final char from, final char to)
	{
		return new RangeMatcher(to, from);
	}

	public static Matcher seq(final Matcher... matchers)
	{
		return new SequenceMatcher(matchers);
	}

	public static Matcher cho(final Matcher... matchers)
	{
		return new ChoiceMatcher(matchers);
	}
	
	public static Matcher longest(final Matcher... matchers)
	{
		return new LongestMatcher(matchers);
	}
	
	public static Matcher ex(final Matcher matcher, final Matcher... filters)
	{
		return new ExcludingMatcher(matcher, filters);
	}

	// ============================================================================================
	// === SIMPLE REPETITION MATCHERS
	// ============================================================================================
	
	public static Matcher rep(final Matcher matchers)
	{
		return rep(0, Integer.MAX_VALUE, matchers);
	}

	public static Matcher rep(final int num, final Matcher matchers)
	{
		return rep(num, num, matchers);
	}

	public static Matcher min(final int min, final Matcher matchers)
	{
		return rep(min, Integer.MAX_VALUE, matchers);
	}

	public static Matcher max(final int max, final Matcher matchers)
	{
		return rep(0, max, matchers);
	}

	public static Matcher rep(final int min, final int max, final Matcher matcher)
	{
		return new RepetitionMatcher(min, max, matcher);
	}
	
	public static Matcher lrep(final Matcher matchers)
	{
		return new LongestMatcher(rep(matchers));
	}

	public static Matcher lrep(final int num, final Matcher matchers)
	{
		return new LongestMatcher(rep(num, matchers));
	}

	public static Matcher lmin(final int min, final Matcher matchers)
	{
		return new LongestMatcher(min(min, matchers));
	}

	public static Matcher lmax(final int max, final Matcher matchers)
	{
		return new LongestMatcher(max(max, matchers));
	}

	public static Matcher lrep(final int min, final int max, final Matcher matcher)
	{
		return new LongestMatcher(rep(min, max, matcher));
	}

	// ============================================================================================
	// === REPETITION OF SEQUENCE MATCHERS
	// ============================================================================================
	
	public static Matcher reps(final Matcher... matchers)
	{
		return reps(0, Integer.MAX_VALUE, matchers);
	}

	public static Matcher reps(final int num, final Matcher... matchers)
	{
		return reps(num, num, matchers);
	}

	public static Matcher mins(final int min, final Matcher... matchers)
	{
		return reps(min, Integer.MAX_VALUE, matchers);
	}

	public static Matcher maxs(final int max, final Matcher... matchers)
	{
		return reps(0, max, matchers);
	}

	public static Matcher reps(final int min, final int max, final Matcher... matchers)
	{
		return new RepetitionMatcher(min, max, RepetitionMatcher.Type.SEQUENCE, matchers);
	}
	
	public static Matcher lreps(final Matcher... matchers)
	{
		return new LongestMatcher(reps(matchers));
	}

	public static Matcher lreps(final int num, final Matcher... matchers)
	{
		return new LongestMatcher(reps(num, matchers));
	}

	public static Matcher lmins(final int min, final Matcher... matchers)
	{
		return new LongestMatcher(mins(min, matchers));
	}

	public static Matcher lmaxs(final int max, final Matcher... matchers)
	{
		return new LongestMatcher(maxs(max, matchers));
	}

	public static Matcher lreps(final int min, final int max, final Matcher... matchers)
	{
		return new LongestMatcher(reps(min, max, matchers));
	}
	
	

	// ============================================================================================
	// === REPETITION OF CHOICE MATCHERS
	// ============================================================================================
	
	public static Matcher repc(final Matcher... matchers)
	{
		return repc(0, Integer.MAX_VALUE, matchers);
	}

	public static Matcher repc(final int num, final Matcher... matchers)
	{
		return repc(num, num, matchers);
	}

	public static Matcher minc(final int min, final Matcher... matchers)
	{
		return repc(min, Integer.MAX_VALUE, matchers);
	}

	public static Matcher maxc(final int max, final Matcher... matchers)
	{
		return repc(0, max, matchers);
	}

	public static Matcher repc(final int min, final int max, final Matcher... matchers)
	{
		return new RepetitionMatcher(min, max, RepetitionMatcher.Type.CHOICE, matchers);
	}
	
	public static Matcher lrepc(final Matcher... matchers)
	{
		return new LongestMatcher(repc(matchers));
	}

	public static Matcher lrepc(final int num, final Matcher... matchers)
	{
		return new LongestMatcher(repc(num, matchers));
	}

	public static Matcher lminc(final int min, final Matcher... matchers)
	{
		return new LongestMatcher(minc(min, matchers));
	}

	public static Matcher lmaxc(final int max, final Matcher... matchers)
	{
		return new LongestMatcher(maxc(max, matchers));
	}

	public static Matcher lrepc(final int min, final int max, final Matcher... matchers)
	{
		return new LongestMatcher(repc(min, max, matchers));
	}

	// ============================================================================================
	// === OPTIONAL MATCHERS
	// ============================================================================================

	public static Matcher opt(final Matcher matcher)
	{
		return cho(matcher, str(""));
	}

	public static Matcher opts(final Matcher... matchers)
	{
		return cho(seq(matchers), str(""));
	}

	public static Matcher optc(final Matcher... matchers)
	{
		return cho(cho(matchers), str(""));
	}

	public static <T> Matcher[] tail(final Matcher[] array)
	{
		return Arrays.asList(array).subList(1, array.length).toArray(new Matcher[] {});
	}

	// ============================================================================================
	// === LONGEST MATCHERS
	// ============================================================================================

	public static Matcher lseq(final Matcher... matchers)
	{
		return longest(seq(matchers));
	}

	public static Matcher lcho(final Matcher... matchers)
	{
		return longest(cho(matchers));
	}
}
