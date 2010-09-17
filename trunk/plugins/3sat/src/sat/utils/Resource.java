package sat.utils;

import java.util.TreeMap;

/**
 * An object that contains properties.
 * Similar in concept to an RDF Resource.
 */
@SuppressWarnings("unchecked")
public class Resource {
	
	private TreeMap<Property<?>, Object> _properties;
	
	public Resource() { }
	
	public <T> T getProperty(Property<T> property) {
		if (_properties == null)
			return null;
		T t= (T) _properties.get(property);
		if (t == null) {
			t= property.getDefaultValue(this);
			if (t != null)
				setProperty(property, t);
		}
		return t;
	}
	
	public <T> void setProperty(Property<T> property, T value) {
		if (_properties == null)
			_properties= new TreeMap<Property<?>, Object>();
		_properties.put(property, value);
	}
	
}
