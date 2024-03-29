package com.googlecode.meteorframework.core.impl;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Lookup;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;

/**
 * Implements @Service injection.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(Lookup.class)
@SuppressWarnings("unchecked")
@ModelElement public class LookupAnnotationHandler extends InjectAnnotationHandler {
	
	private String _uri;
	
	@Override
	public boolean initialize(Scope repository, Object annotation, Object target, int parameterIndex)
	{
		boolean isSupported= super.initialize(repository, annotation, target, parameterIndex);
		_uri= ((Lookup)annotation).value();
		return isSupported;
	}
	@Override
	public void inject(Object decorator, Object delegate, BindingContext bindingContext)
	{
		Resource service= RepositoryImpl.findResourceByURI(_scopeReference.get(), _uri);
		if (service == null)
			throw new MeteorException("Error injecting value into field "+_field.get().getGenericType()+". Resource not found:"+_uri);
		try {
			_field.get().set(decorator, ObjectImpl.getObjectImpl(service).internalCast(_javaType.get()));
		} 
		catch (Throwable e) {
			throw MeteorException.getMeteorException("Error injecting value into field "+_field.get().getGenericType(),e);
		}
	}
}
