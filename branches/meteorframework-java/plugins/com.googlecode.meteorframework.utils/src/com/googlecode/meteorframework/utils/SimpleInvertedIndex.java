package com.googlecode.meteorframework.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SimpleInvertedIndex<T,D>
{
	private static final long	serialVersionUID	= 1L;
	
	private HashMap<T, Set<D>> _documentsByToken= new HashMap<T, Set<D>>();
	
	/**
	 * Index a document.
	 * @param tokens the tokens that appear in the given document
	 */
	public void put(Set<T> tokens, D document) {
		for (T token : tokens) {
			Set<D> documentsForToken= _documentsByToken.get(token);
			if (documentsForToken == null) {
				documentsForToken= new HashSet<D>();
				_documentsByToken.put(token, documentsForToken);
			}
			documentsForToken.add(document);
		}
	};
	
	/**
	 * Return all the documents that contain the given tokens.
	 */
	public Set<D> get(Iterable<T> tokens) {
		
		if (tokens == null)
			return Collections.EMPTY_SET;
			
		Set<D> found= null;
		for (T token : tokens) {
			Set<D> documentsForToken= _documentsByToken.get(token);
			if (documentsForToken == null)
				return Collections.EMPTY_SET;
			
			if (found == null) {
				found= new HashSet<D>();
				found.addAll(documentsForToken);
			}
			else {
				for (Iterator<D> i= found.iterator(); i.hasNext();) {
					D document= i.next();
					if (!documentsForToken.contains(document))
						i.remove();
				}
			}
			
			if (found.isEmpty())
				return found;
		}
		
		if (found == null)
			return Collections.EMPTY_SET;
		
		return found;
	}
}
