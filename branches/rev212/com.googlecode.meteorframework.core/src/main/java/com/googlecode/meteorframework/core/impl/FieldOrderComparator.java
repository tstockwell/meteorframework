package com.googlecode.meteorframework.core.impl;

import java.util.Comparator;
import java.util.List;

import com.googlecode.meteorframework.core.Property;


public class FieldOrderComparator implements Comparator<Property<?>>
{
	List<Property<?>> _fieldOrder;
	
	public FieldOrderComparator(List<Property<?>> fieldOrder) {
		_fieldOrder= fieldOrder;
	}

	@Override
	public int compare(Property<?> o1, Property<?> o2)
	{
		int i1= _fieldOrder.indexOf(o1);
		int i2= _fieldOrder.indexOf(o2);
		
		if (i1 < 0) {
			if (i2 < 0) {
				return 0;
			}
			return 1;
		}

		if (i2 < 0) {
			return -1;
		}
		else
			return 0;
	}

}
