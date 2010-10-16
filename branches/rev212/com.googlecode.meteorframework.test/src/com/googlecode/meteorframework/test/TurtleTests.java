package com.googlecode.meteorframework.test;

import static com.googlecode.meteorframework.core.utils.TurtleGrammar.DOCUMENT;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.LITERAL;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.OBJECT_LIST;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.PREDICATE_VALUES;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.STATEMENT;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.SUBJECT;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.TRIPLES;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.URIREF;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.VERB;

import java.util.List;

import com.googlecode.meteorframework.core.utils.TurtleGrammar;
import com.googlecode.meteorframework.parser.Grammar;
import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.Matchers;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.Navigation;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TurtleTests
extends TestCase
{
	public void testBasicTriples() throws Exception {
		
		String document= 	
			"@prefix resource: <meteor:com.googlecode.meteorframework.Resource.>.\n" +
			"@prefix test: <meteor:com.googlecode.meteorframework.test.>.\n" +
			"\n" +
			"test:CustomerFromManifest resource:type test:Customer ;\n" +
			"			resource:description \"A Customer defined in a Meteor manifest\" .\n\n\n\n\n";
		
		Matcher literalMatcher= TurtleGrammar.getMatcher(LITERAL);		
		Assert.assertEquals(1, literalMatcher.match("\"A Customer defined in a Meteor manifest\"").matches.size());
		
		String testgrammar= "uriref ::= (\"<\" relativeURI \">\")\n" +
				"relativeURI ::= (anychar {anychar})\n" +
				"anychar ::= (%alpha% | %digit% | \" \" | \":\" | \".\")";  
		Matcher relativeURIMatcher= Grammar.generate(testgrammar, "relativeURI");		
		Assert.assertEquals(1, Matchers.fullMatch(relativeURIMatcher, "meteor:com.googlecode.meteorframework.TestBindType.instance").matches.size());
		
		Matcher urirefMatcher= TurtleGrammar.getMatcher(URIREF);		
		Assert.assertEquals(1, urirefMatcher.match("<meteor:com.googlecode.meteorframework.TestBindType.instance>").matches.size());
		
		Matcher subjectMatcher= TurtleGrammar.getMatcher(SUBJECT);		
		Assert.assertEquals(1, subjectMatcher.match("<meteor:com.googlecode.meteorframework.TestBindType.instance>").matches.size());
		Assert.assertEquals(1, subjectMatcher.match("test:CustomerFromManifest").matches.size());
		
		Matcher verbMatcher= TurtleGrammar.getMatcher(VERB);	
		Assert.assertEquals(1, verbMatcher.match("resource:type").matches.size());
		
		Matcher objectListMatcher= TurtleGrammar.getMatcher(OBJECT_LIST);		
		Assert.assertEquals(1, objectListMatcher.match("webbench:WebbenchModule").matches.size());
		
		Matcher valuesMatcher= TurtleGrammar.getMatcher(PREDICATE_VALUES);		
		Assert.assertEquals(1, valuesMatcher.match("resource:type webbench:WebbenchModule").matches.size());
		Assert.assertEquals(1, valuesMatcher.match("resource:description \"A Customer defined in a Meteor manifest\"").matches.size());
		Assert.assertEquals(1, valuesMatcher.match("resource:type test:Customer ;\n\t\t\tresource:description \"A Customer defined in a Meteor manifest\"").matches.size());
		
		Matcher triplesMatcher= TurtleGrammar.getMatcher(TRIPLES);
		String tripleDoc= "<meteor:com.googlecode.meteorframework.TestBindType.instance> resource:type webbench:WebbenchModule"; 
		Assert.assertEquals(1, triplesMatcher.match(tripleDoc).matches.size());
		tripleDoc= "test:CustomerFromManifest resource:type test:Customer ;\n\t\t\tresource:description \"A Customer defined in a Meteor manifest\"";
		Assert.assertEquals(1, triplesMatcher.match(tripleDoc).matches.size());
		
		Matcher statementMatcher= TurtleGrammar.getMatcher(STATEMENT);
		String statementDoc= tripleDoc+" ."; 
		Assert.assertEquals(1, statementMatcher.match(statementDoc).matches.size());
		
		Matcher matcher= TurtleGrammar.getMatcher(DOCUMENT);		
		MatchResults results= matcher.match(document);
		Assert.assertEquals(1, results.matches.size());
		AbstractNode rootNode= results.matches.get(0);
		
		// the AST should have 3 statements in it
		List<AbstractNode> statements= Navigation.findAllById(rootNode, "statement");
		Assert.assertEquals(3, statements.size());
		

		Matcher predicateValuesMatcher= TurtleGrammar.getMatcher(TurtleGrammar.PREDICATE_VALUES);
		document= "meteor:Resource.type jdbc:JDBCDriverDescriptor"; 
		results= predicateValuesMatcher.match(document);
		Assert.assertFalse(results.success());
		Assert.assertEquals(15, results.position);
		Assert.assertEquals("Expected whitespace", results.errorMsg);
		

		document= "h2:H2Driver meteor:Resource.type jdbc:JDBCDriverDescriptor"; 
		results= triplesMatcher.match(document);
		Assert.assertFalse(results.success());
		Assert.assertEquals(27, results.position);
		Assert.assertEquals("Expected whitespace", results.errorMsg);
		

		document= "h2:H2Driver meteor:Resource.type jdbc:JDBCDriverDescriptor."; 
		results= statementMatcher.match(document);
		Assert.assertFalse(results.success());
		Assert.assertEquals(27, results.position);
		Assert.assertEquals("Expected whitespace", results.errorMsg);
		
		document= "h2:H2Driver meteor:Resource.type jdbc:JDBCDriverDescriptor ;\n" +
			"jdbc:JDBCDriverDescriptor.protocol \"jdbc:h2\" ;\n" +
			"jdbc:JDBCDriverDescriptor.driverClass \"org.h2.Driver\" .\n\n\n\n\n\n\n"; 
		results= matcher.match(document);
		Assert.assertFalse(results.success());
		Assert.assertEquals(27, results.position);
		Assert.assertEquals("Expected whitespace", results.errorMsg);
		
	}
	
}
