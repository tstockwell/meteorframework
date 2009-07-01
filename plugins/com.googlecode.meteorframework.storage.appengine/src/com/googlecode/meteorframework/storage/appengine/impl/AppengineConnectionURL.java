package com.googlecode.meteorframework.storage.appengine.impl;

import static com.googlecode.meteorframework.parser.Parsers.lreps;
import static com.googlecode.meteorframework.parser.Parsers.opt;
import static com.googlecode.meteorframework.parser.Parsers.seq;
import static com.googlecode.meteorframework.parser.Parsers.str;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.meteorframework.parser.MatchResults;
import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.NameMatcher;
import com.googlecode.meteorframework.parser.matcher.NamedMatcher;
import com.googlecode.meteorframework.parser.node.AbstractNode;
import com.googlecode.meteorframework.parser.node.Navigation;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageConfiguration;


/**
 * An adapter class that parses an appengine connection url and provides the 
 * GAE root entity names and connection parameters.
 * @author Ted Stockwell
 */
public class AppengineConnectionURL {
	
	static final Matcher _matcher;
	static final String ROOT= "root";
	static final String PARAMETER= "parameter";
	static final String NAME= "pName";
	static final String VALUE= "pValue";
	
	static {
		Matcher protocol= str(AppengineStorageConfiguration.APPENGINE_STORAGE_PROTOCOL);
		Matcher root= new NamedMatcher(ROOT).define(new NameMatcher());
		Matcher pValue= new NamedMatcher(VALUE).define(new NameMatcher());
		Matcher pName= new NamedMatcher(NAME).define(new NameMatcher());
		Matcher parameter= new NamedMatcher(PARAMETER).define(seq(pName, str("="), pValue));
		_matcher= seq(protocol, str("/"), root, opt(seq(str("?"), parameter, lreps(seq(str(","), parameter))))); 
	}
	
	private String _url;
	private String _rootURI;
	private HashMap<String, String> _parameters= new HashMap<String, String>();
	
	public AppengineConnectionURL(String url) 
	throws ParseException
	{
		_url= url;
		MatchResults results = _matcher.match(url);
		AbstractNode ast= null;
		for (AbstractNode result : results.matches)
			if (url.length() <= result.end)
				ast= result;
		if (ast == null)
		{
			int end= results.position;
			String msg= "Not a valid Appengine connection URL:"+url;
			msg+= "\nError at position "+results.position;
			if (results.errorMsg != null)
				msg+= "\n"+results.errorMsg;
			throw new ParseException(msg, end);
		}
		_rootURI= Navigation.findById(ast, ROOT).getText();
		
		List<AbstractNode> parameterNodes = Navigation.findAllById(ast, PARAMETER);
		for (AbstractNode parameterNode : parameterNodes)
		{
			String name= Navigation.findById(parameterNode, NAME).getText();
			String value= Navigation.findById(parameterNode, VALUE).getText();
			_parameters.put(name, value);
		}
	}
	
	public String getURL()
	{
		return _url;
	}
	public String getRootURI()
	{
		return _rootURI;
	}
	public Map<String, String> getParameters()
	{
		return _parameters;
	}

}
