package com.googlecode.meteorframework.core;

import java.util.List;
import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.IsFunction;



/**
 * Encapsulates information about a Meteor type.
 * The Meteor equivalent of a Java class.
 * In Meteor classes are called types in order to avoid conflicts with the Java 
 * class keyword.
 *  
 * @author Ted Stockwell
 * 
 * @param <T> The Java Type to which this Type applies.
 */
@com.googlecode.meteorframework.core.annotation.ModelElement 
public interface Type<T> 
extends Resource, ModelElement 
{
	
	public Class<T> getJavaType();
	
	@InverseOf(CoreNS.Type.subTypes) 
	public Set<Type<?>> getSuperTypes();
	
	@InverseOf(CoreNS.Type.superTypes) 
	public Set<Type<? extends T>> getSubTypes();

	@InverseOf(CoreNS.Property.domain) 
	public Set<Property<?>> getDeclaredProperties();

	@InverseOf(CoreNS.Function.domain) 
	public Set<Function<?>> getDeclaredFunctions();
	
	@IsFunction
	public boolean isAssignableFrom(Type<? extends T> type);
	
	@IsFunction
	public boolean isAssignableFrom(Class<?> javaType);
	
	
	/**
	 * Denotes the Types that this Type extends.
	 */
	@InverseOf(CoreNS.Type.extensions) 
	public Set<Type<? extends T>> getExtensionOf();
	public void addExtensionOf(Type<? extends T> extensionOf);
	public void removeExtensionOf(Type<? extends T> extensionOf);
	public void clearExtensionOf();

	/**
	 * Denotes the Types that extend this Type.
	 */	
	@InverseOf(CoreNS.Type.extensionOf) 
	public Set<Type<? extends T>> getExtensions();
	

	
	
	/**
	 * Denotes the Roles that this Type can play.
	 */
	public Set<Type<?>> getRolesPlayed();

	/**
	 * Denotes the Types that play this role.
	 */
	public Set<Type<?>> getPlayedBy();
	
	/**
	 * Indicates that only a single instance of this type should ever be created.
	 */
	public boolean isSingleton();
	public boolean getSingleton();
	public void setSingleton(boolean value);

	/**
	 * Indicates that this type represents a scalar object.
	 * A scalar is an atomic unit of data, like a String or an Integer.
	 */
	public boolean getScalar();
	public void setScalar(boolean value);
	public boolean isScalar();
	
	/**
	 * Returns true if this type is that same as, or a super type of, the 
	 * given type. 
	 */
	@IsFunction
	public boolean isSuperTypeOf(Type<?> type);
	
	@InverseOf(CoreNS.Namespace.types) 
	public Namespace getNamespace();
	
	public void setNamespace(Namespace namespace);

	/**
	 * Get all properties that have default values.
	 * Does not include properties inherited from supertypes. 
	 * @return
	 */
	public Set<Property<?>> getDeclaredDefaultedProperties();
	
	/**
	 * Get all properties that have default values.
	 * Includes properties inherited from supertypes. 
	 */
	public Set<Property<?>> getAllDefaultedProperties();

	/**
	 * Get all properties that belong to this type, including those 
	 * declared in subtypes and extensions.
	 */
	public Set<Property<?>> getAllProperties();
	
	/**
	 * Hints as to how properties should be arranged on a form.
	 * Properties should appear in the order specified by the fieldOrder property.
	 * Any properties not included appear after the listed properties, in no 
	 * particular order. 
	 */
	public List<Property<?>> getFieldOrder();

}




