package com.googlecode.meteorframework.core;

import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement public interface NumericProperty<T extends Number>
extends Property<T>
{
	public T getMaximumValue();
	public T getMinimumValue();
}
