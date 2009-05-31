package com.googlecode.meteorframework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ArrayListSet<E>
extends ArrayList<E>
implements Set<E>
{
	public ArrayListSet()
	{
		super();
	}

	public ArrayListSet(Collection<? extends E> c)
	{
		super(c);
	}

	public ArrayListSet(int initialCapacity)
	{
		super(initialCapacity);
	}

	private static final long serialVersionUID = 1L;
	
	@Override public boolean add(E e) {
		if (!this.contains(e))
			return super.add(e);
		return false;
	};
	
	@Override public void add(int index, E element) {
		if (this.contains(element)) {
			if (indexOf(element) != index)
				throw new RuntimeException("The given element already exists in this set at a different position.");
			return;
		}
		super.add(index, element);
    }
    
    @Override public boolean addAll(Collection<? extends E> c)
    {
    	boolean changed= false;
    	for (E e: c) {
    		if (add(e))
    			changed= true;
    	}
    	return changed;
    }
    
    @Override public boolean addAll(int index, Collection<? extends E> c)
    {
    	boolean changed= false;
    	for (Iterator<? extends E> i= c.iterator(); i.hasNext(); index++) {
    		E e= i.next();
    		if (this.contains(e)) {
    			if (indexOf(e) != index)
    				throw new RuntimeException("An element already exists in this set at a different position.");
    			continue;
    		}
    		super.add(index, e);
    	}
    	return changed;
    }
}
