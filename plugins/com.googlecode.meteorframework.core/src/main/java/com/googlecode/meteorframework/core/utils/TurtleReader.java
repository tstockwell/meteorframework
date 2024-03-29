package com.googlecode.meteorframework.core.utils;

import static com.googlecode.meteorframework.core.utils.TurtleGrammar.OBJECT;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.PREDICATE_VALUES;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.PREFIX_ID;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.PREFIX_NAME;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.SUBJECT;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.TRIPLES;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.URIREF;
import static com.googlecode.meteorframework.core.utils.TurtleGrammar.VERB;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.Navigation;
import com.googlecode.meteorframework.parser.util.Files;


/**
 * Reads metadata from Turtle RDF files and loads the data into a Meteor Scope.
 * 
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
public class TurtleReader
{
	
	public static class Statement {
		public String subject;
		public String predicate;
		public String object;
		public Statement(String subject, String predicate, String object)
		{
			this.subject= subject;
			this.predicate= predicate;
			this.object= object;
		}
	}

	static private final String COLON = ":";

	private AbstractNode _ast;
	private int _position;
	private HashMap<String, String> _prefixes = new HashMap<String, String>();
	private List<Statement> _statements; 

	public TurtleReader(URL url) throws IOException, ParseException
	{
		this(Files.readAsString(url), url);
	}
	public TurtleReader(String input) throws ParseException
	{
		this(input, null);
	}
	public TurtleReader(String input, URL url) throws ParseException
	{
		MatchResults results = TurtleGrammar.getMatcher().match(input);
		for (AbstractNode ast : results.matches)
			if (input.length() <= ast.end)
				_ast= ast;
		if (_ast == null)
		{
			int line= 0;
			int column= 0;
			int end= results.position;
			String errorMsg= results.errorMsg;
			
			if (results.errorMsg == null && !results.matches.isEmpty())
			{
				AbstractNode node= results.matches.get(0);
				String statementWithError= input.substring(node.end);
				MatchResults results2=  TurtleGrammar.getMatcher().match(statementWithError);
				errorMsg= results2.errorMsg;
				end= node.end + results2.position;
			}
			
			int pos = -1;
			do
			{
				int p = input.indexOf('\n', pos+1);
				if (p < 0 || end < p)
					break;
				pos= p;
				line++;
			}
			while (true);
			column= end - pos;
			line++;
			
			String msg= "Resource does not contain a valid Turtle document";
			if (url != null)
				msg+= ": " + url;
			msg+= "\nError at line "+line+", column "+column;
			msg+= "\n"+errorMsg;
			throw new ParseException(msg, end);
		}
		_ast = results.matches.get(0);

		initializePrefixes();
		_statements= createStatements();
	}

	private void initializePrefixes() throws ParseException
	{
		List<AbstractNode> prefixNodes = Navigation.findAllById(_ast, PREFIX_ID);
		for (AbstractNode prefixNode : prefixNodes)
		{
			AbstractNode uriNode = Navigation.findAllById(prefixNode, URIREF).get(0);
			AbstractNode nameNode = Navigation.findAllById(prefixNode, PREFIX_NAME).get(0);
			_prefixes.put(nameNode.getText(), getURI(uriNode.getText()));
		}
	}
	
	protected List<Statement> createStatements() throws ParseException 
	{
		ArrayList<Statement> statements= new ArrayList<Statement>();
		List<AbstractNode> triplesNodes = Navigation.findAllById(_ast, TRIPLES);
		for (AbstractNode triplesNode : triplesNodes)
		{
			String subjectURI = getURI(Navigation.findById(triplesNode, SUBJECT).getText());
			List<AbstractNode> valueNodes = Navigation.findAllById(triplesNode, PREDICATE_VALUES);

			for (AbstractNode valueNode : valueNodes)
			{
				String predicate = Navigation.findById(valueNode, VERB).getText();
				if ("a".equalsIgnoreCase(predicate))
					predicate= "<"+CoreNS.Resource.type+">";

				List<AbstractNode> objectNodes = Navigation.findAllById(valueNode, OBJECT);
				for (AbstractNode objectNode : objectNodes)
				{
					String value = objectNode.getText();
					String propertyURI = getURI(predicate);
					
					statements.add(new Statement(subjectURI, propertyURI, value));
				}
			}
		}
		return statements;
	}
	public List<Statement> getStatements()  
	{
		return _statements;
	}

	public void addMetadataToScope(Scope scope) throws ParseException
	{
		List<Statement> statements= getStatements();
		for (Statement statement : statements)
		{

			Property<?> property = scope.findResourceByURI(statement.predicate, Property.class);
			if (property == null)
				throw new ParseException("No such property:" + statement.predicate, _position);

			Resource resource = scope.findResourceByURI(statement.subject);
			boolean addToRepository = false;
			if (resource == null)
			{
				if (!CoreNS.Resource.type.equals(statement.predicate))
					throw new ParseException("Meteor N3 reader requires " +
							CoreNS.Resource.type + 
							" to be the first listed property", _position);

				String typeURI = getURI(statement.object);
				Type<?> meteorType = scope.findResourceByURI(typeURI, Type.class);
				if (meteorType == null)
					throw new ParseException("No such type:" + typeURI, _position);
				resource = (Resource) scope.getInstance(meteorType.getJavaType());
				resource.setURI(statement.subject);
				addToRepository = true;
			}

			resource.setProperty((Property<Object>)property, getValue(statement.object));

			if (addToRepository)
				scope.addResource(resource);
		}
	}

	private Object getValue(String value) throws ParseException
	{

		// strip off quotes around literals
		if (value.startsWith("\""))
		{
			if (!value.endsWith("\""))
				throw new ParseException("Unterminated literal", _position);
			return value.substring(1, value.length() - 1);
		} 
		else if (value.startsWith("\'"))
		{
			if (!value.endsWith("\'"))
				throw new ParseException("Unterminated literal", _position);
			return value.substring(1, value.length() - 1);
		} 
		else if (value.startsWith("<"))
		{
			if (!value.endsWith(">"))
				throw new ParseException("Unterminated URI", _position);
			return value.substring(1, value.length() - 1);
		} else
			value = replacePrefix(value);

		return value;
	}

	private String getURI(String subject) throws ParseException
	{

		// if plain URL then strip off lt/gt signs
		if (subject.startsWith("<"))
		{
			if (!subject.endsWith(">"))
				throw new ParseException("Expected url that ends with '>'", _position);
			return subject.substring(1, subject.length() - 1);
		}

		subject = replacePrefix(subject);

		return subject;
	}

	private String replacePrefix(String subject) throws ParseException
	{

		// replace any prefix
		int i = subject.indexOf(COLON);
		if (0 <= i)
		{
			String prefix = subject.substring(0, i);
			String replacement = _prefixes.get(prefix);
			if (replacement == null)
				throw new ParseException("Unrecognised prefix:" + prefix, _position);

			subject = replacement + subject.substring(i + 1);
		}

		return subject;
	}
}
