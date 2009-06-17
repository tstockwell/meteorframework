package com.googlecode.meteorframework.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.binding.Formatted;
import com.googlecode.meteorframework.utils.ArrayListSet;

/**
 * A set of bindings.  
 * When determining what decorators and interceptors to use to handle a method 
 * invocation, Meteor filters out decorators and interceptors that do not 
 * match the bindings in the current context.
 * 
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
@ModelElement public class BindingContext
extends ArrayListSet<BindingType>
{
	private static final long	serialVersionUID	= 1L;

	public static final BindingContext EMPTY_BINDINGS= new BindingContext(); 
	
	private static final HashMap<Set<BindingType>, BindingContext> __cache= 
		new HashMap<Set<BindingType>, BindingContext>();
	
	private BindingType[]  _array;
	private int _hashcode= 0;
	
	private BindingContext(Set<BindingType> bindingTypes) {
		if (bindingTypes != null) {
			for (BindingType bindingType : bindingTypes)
				super.add(bindingType);
		}
	}
	private BindingContext() {
	}
	
	@Override public int hashCode()
	{
		if (_hashcode == 0)
			_hashcode= super.hashCode();
		return _hashcode;
	}
	
	public BindingType[] toArray() {
		if (_array == null) {
			_array= new BindingType[size()];
			toArray(_array);
		}
		return _array;
	}
	
	public static final BindingContext getBindingContext(BindingType... bindingTypes) {
		return EMPTY_BINDINGS.union(bindingTypes);
	}
	
	public static final BindingContext getBindingContext(Set<BindingType> bindingTypes) {
		BindingContext bindingContext= __cache.get(bindingTypes);
		if (bindingContext == null) {
			bindingContext= new BindingContext(bindingTypes);
			__cache.put(bindingTypes, bindingContext);
		}
		return bindingContext;
	}
	
	
	
	
//	public Bindings(BindingType[] bindings) {
//		_BindingTypes= (bindings == null || bindings.length <= 0) ? EMPTY_BindingType_ARRAY : bindings.clone();
//	}
//	public Bindings() {
//		_BindingTypes= EMPTY_BindingType_ARRAY;
//	}
//
	
	public boolean isSuperSetOf(BindingContext bindingContext) {
		if (bindingContext.isEmpty()) 
			return true;
		if (isEmpty())
			return false;
		for (BindingType bindingType : bindingContext) {
			if (!contains(bindingType))
				return false;
		}
		return true;
	}

	public BindingContext union(BindingContext bindingContext)
	{
		if (bindingContext == null)
			return this;
		
		ArrayListSet<BindingType> union= new ArrayListSet<BindingType>(this);
		union.addAll(bindingContext);
		return BindingContext.getBindingContext(union);
	}

	public BindingContext union(BindingType... bindingTypes)
	{
		if (bindingTypes == null || bindingTypes.length <= 0)
			return this;
		
		HashSet<BindingType> union= null;
		for (BindingType bindingType : bindingTypes) {
			if (!contains(bindingType)) {
				if (union == null)
					union= new HashSet<BindingType>();
				union.add(bindingType);
			}
		}
		
		return (union == null) ? this : BindingContext.getBindingContext(union);
	}
	
	@Override
	public boolean add(BindingType e)
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public void add(int index, BindingType element)
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(Collection<? extends BindingType> c)
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(int index, Collection<? extends BindingType> c)
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	public <T> List<T> findInstances(Class<T> class1) 
	{
		if (_array == null)
			return Collections.EMPTY_LIST;
		ArrayList list= new ArrayList();
		for (int i= 0; i < _array.length; i++)
		{
			if (((Resource)_array[i]).getType().isInstanceOf(class1))
				list.add(_array[i]);
		}
		return list;
	}
}
