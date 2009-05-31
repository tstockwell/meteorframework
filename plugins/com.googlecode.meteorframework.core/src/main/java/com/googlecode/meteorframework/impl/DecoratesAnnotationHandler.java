package com.googlecode.meteorframework.impl;

import com.googlecode.meteorframework.BindingContext;
import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.ProcessesAnnotations;

/**
 * Implements @Decorates injection.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(Decorates.class)
@SuppressWarnings("unchecked")
@Model public class DecoratesAnnotationHandler extends InjectAnnotationHandler {
	
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
