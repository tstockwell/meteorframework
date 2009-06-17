package com.googlecode.meteorframework.storage.triplestore;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * A service that Encodes/Decodes values for storage in the triple store.
 * The triplestore saves all data as strings and therefore requires numeric 
 * and date data to be encoded so that stored data preserves its natural order.
 * 
 * Since this is a Meteor service it is extendable/customizable by third-party plugins
 * 
 * @author ted stockwell
 */
@ModelElement public interface TripleStoreCodec extends Service {
	
	/**
	 * @param stringValue The value from the triplestore to decode
	 * @param type The type to which the value should be converted	
	 */
	public <T> T decode(String stringValue, Class<T> type);

	/**
	 * @param value The value to encode for storage.
	 */
	public String encode(Object value);
}
