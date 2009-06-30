package com.googlecode.meteorframework.storage.appengine.impl;

import com.googlecode.meteorframework.parser.matcher.Matcher;
import com.googlecode.meteorframework.parser.matcher.NamedMatcher;
import com.googlecode.meteorframework.storage.appengine.AppengineStorageConfiguration;

import static com.googlecode.meteorframework.parser.Parsers.*;


public class AppengineConnectionURLParser {
	
	static Matcher _matcher;
	static final String ROOT= "root";
	
	static {
		Matcher protocol= str(AppengineStorageConfiguration.APPENGINE_STORAGE_PROTOCOL);
		Matcher root= new NamedMatcher(ROOT, new NamedMatcher)
		Matcher roots;
		Matcher queryString;
		_matcher= seq(protocol, roots, opt(queryString) ); 
	}
	
	public AppengineConnectionURLParser(String url)
	{
		
	}

}
