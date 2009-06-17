package com.googlecode.meteorframework.core.impl;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.annotation.Decorates;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;

/**
 * Implements @Decorates injection.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(Decorates.class)
@SuppressWarnings("unchecked")
@ModelElement public class DecoratesAnnotationHandler extends InjectAnnotationHandler {
	
	@Override
	public void inject(Object decorator, Object delegate, BindingContext bindingContext)
	{
		try {
			ObjectImpl impl= ObjectImpl.getObjectImpl(delegate);
			_field.get().set(decorator, impl.internalCast(_javaType.get()));
		} 
		catch (Throwable e) {
			throw MeteorException.getMeteorException("Error injecting value into field "+_field.get().getGenericType(),e);
		}
	}
}
