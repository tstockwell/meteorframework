package com.googlecode.meteorframework.core;

import java.util.Set;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;


/**
 * @author Ted Stockwell
 *
 * @param <T> The type of objects associated with this properties range.
 */
@Model public interface Property<T> extends Resource, ModelElement {
	
	
//	public void initFromJavaTypes(java.lang.reflect.Method getMethod, java.lang.reflect.Method setMethod);
	
	
	public Object getDefaultValue();
	public void setDefaultValue(Object p_defaultValue);
	
	public Type<?> getRange();
	public void setRange(Type<?> p_range);
	
	/**
	 * Indicates a property that is not to be persisted or serialized.
	 */
	public void setTemporal(boolean value);
	public boolean getTemporal();
	public boolean isTemporal();
	
	/**
	 * Indicates that this property references other objects.
	 * Instead of 'reference' this property could have been called 
	 * 'association' since it indicates that this property defines 
	 * a UML-like association relationship between two types.   
	 */
	public void setReference(boolean value);
	public boolean getReference();
	public boolean isReference();
	
	public void setReadOnly(boolean value);
	public boolean getReadOnly();
	public boolean isReadOnly();
	
	public void setWriteOnly(boolean value);
	public boolean getWriteOnly();
	public boolean isWriteOnly();
	
	public void setWriteOnce(boolean value);
	public boolean getWriteOnce();
	public boolean isWriteOnce();
	
	/**
	 * Indicates if a property is multivalued.
	 * In Meteor multivalued properties return a Collection object that 
	 * contains all the propery values. 
	 */
	public void setMany(boolean value);
	public boolean getMany();
	public boolean isMany();
	
	/**
	 * Indicates if a multivalued property si ordered.
	 * In Meteor ordered multivalued properties return a List object that 
	 * contains all the propery values. 
	 */
	public void setOrdered(boolean value);
	public boolean getOrdered();
	public boolean isOrdered();
	
	/**
	 * Indicates if the values of a multivalued property are unique.
	 * In Meteor unique multivalued properties return a Set object that 
	 * contains all the propery values. 
	 */
	public void setUnique(boolean value);
	public boolean getUnique();
	public boolean isUnique();
	
	/**
	 * Indicates that the lifetime of the objects referenced by this property 
	 * are controlled by the 'owner'.
	 * That is, when the owner is destroyed then the objects referenced by this 
	 * property may be destroyed also. 
	 */
	public void setComposite(boolean value);
	public boolean getComposite();
	public boolean isComposite();
	
	/**
	 * Indicates that this property defines a map between its owner and other objects.
	 * The references to the other objects are 'indexed', that is, you need a 'key' 
	 * to access the objects.
	 * Properties of this type return java.util.Map objects.
	 */
	public void setIndexed(boolean value);
	public boolean getIndexed();
	public boolean isIndexed();
	
	/**
	 * If this property is indexed then this property indicates the type of the 
	 * key values.
	 */
	public void setIndexedType(Type<?> value);
	public Type<?> getIndexedType();
	
	/**
	 * Reference properties have a direction. 
	 * In practice, people often find it useful to define relations in both 
	 * directions.  For instance, persons own cars, cars are owned by persons. 
	 * The inverseOf property can be used to define such an inverse relation 
	 * between properties.
	 * 
	 * Meteor handles inverse properties automatically, that is when a 
	 * property 'X' of a resource 'R1' is set to 'R2' then any inverse 
	 * properties of R2 are set to R1.
	 * 
	 * For instance, in plain Java you would have to write...
	 * 	 	person.getOwnedCars().add(car);
	 *   	car.setOwner(person);
	 * ... but since Meteor handles inverse properties you only need to write this...
	 * 		car.setOwner(person);
	 * ... or this..
	 * 		person.getOwnedCars().add(car);  
	 * ...as long as the ownedCars and owner properties have been properly 
	 * defined as inverse properties.
	 */
	public Set<Property<T>> getInverseOf();
	public void setInverseOf(Set<Property<T>> inverseProperties);
	
	/**
	 * The set of Types to which this property belongs.
	 * May be empty.
	 */
	@InverseOf(MeteorNS.Type.declaredProperties) 
	public Set<Type<?>> getDomain();
	public void setDomain(Set<Type<?>> domainTypes);
	
	
	/**
	 * Equivalent properties always have the same value. 
	 * When a property is set then Meteor will also automatically set all 
	 * equivalent properties.
	 * 
	 * NOTE: Property equivalence is not the same as property equality. 
	 * Equivalent properties have the same values but may have different 
	 * intentional meaning (i.e., denote different concepts). 
	 * Property equality should be expressed with the Resource:sameAs 
	 * construct.
	 */
	@InverseOf(MeteorNS.Property.equivalentOf) 
	public Set<Property<T>> getEquivalentOf();
	public void setEquivalentOf(Set<Property<T>> equivalentProperties);
	
	
}
