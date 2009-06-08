package com.googlecode.meteorframework.storage.triplestore.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;


import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.storage.StorageException;
import com.googlecode.meteorframework.storage.triplestore.TripleStoreCodec;

/**
 * A service that Encodes/Decodes values for storage in the triple store.
 * The triplestore saves all data as strings and therefore requires numeric 
 * and date data to be encoded so that stored data preserves its natural order.
 * 
 * Since this is a Meteor service it is extendable/customizable by third-party plugins
 * 
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
@Decorator public abstract class TripleStoreCodecImpl implements TripleStoreCodec {
	
	@Inject Scope _scope;
	
	/**
	 * @param stringValue The value from the triplestore to decode
	 * @param type The type to which the value should be converted	
	 */
	public <T> T decode(String stringValue, Class<T> type) {
		try {
			if (stringValue == null)
				return null;
			if (String.class.isAssignableFrom(type)) 
				return (T)stringValue;
			if (Boolean.class.isAssignableFrom(type)) 
				return (T)Boolean.valueOf(stringValue);
			if (Date.class.isAssignableFrom(type)) 
				return (T)TripleStoreUtils.getDateFromISO8601String(stringValue);
			if (BigDecimal.class.isAssignableFrom(type)) 
				return (T)StorageNumberCodec.decodeAsBigDecimal(stringValue);
			if (BigInteger.class.isAssignableFrom(type)) 
				return (T)new BigInteger(StorageNumberCodec.decode(stringValue));
			if (Double.class.isAssignableFrom(type)) 
				return (T)new Double(StorageNumberCodec.decode(stringValue));
			if (Float.class.isAssignableFrom(type)) 
				return (T)new Float(StorageNumberCodec.decode(stringValue));
			if (Integer.class.isAssignableFrom(type)) 
				return (T)new Integer(StorageNumberCodec.decode(stringValue));
			if (Long.class.isAssignableFrom(type)) 
				return (T)new Long(StorageNumberCodec.decode(stringValue));
			if (Resource.class.isAssignableFrom(type)) {
				return (T)_scope.findResourceByURI(stringValue, (Class<? extends Resource>)type);
			}
			return (T)stringValue;
		} 
		catch (Throwable e) {
			throw new StorageException(e);
		}
	}

	/**
	 * @param value The value to encode for storage.
	 */
	public String encode(Object value) {
		if (value == null)
			return null;
		if (value instanceof String)
			return (String)value;
		if (value instanceof Boolean)
			return ((Boolean)value).toString();
		if (value instanceof Date)
			return TripleStoreUtils.getDateAsISO8601String((Date)value);
		if (value instanceof BigDecimal)
			return StorageNumberCodec.encode((BigDecimal)value);
		if (value instanceof BigInteger)
			return StorageNumberCodec.encode(new BigDecimal((BigInteger)value));
		if (value instanceof Double)
			return StorageNumberCodec.encode(BigDecimal.valueOf(((Double)value).doubleValue()));
		if (value instanceof Float)
			return StorageNumberCodec.encode(BigDecimal.valueOf(((Float)value).floatValue()));
		if (value instanceof Number)
			return StorageNumberCodec.encode(((Number)value).toString());
		if (value instanceof Resource)
			return ((Resource)value).getURI();
		return value.toString();
	}
	
}
