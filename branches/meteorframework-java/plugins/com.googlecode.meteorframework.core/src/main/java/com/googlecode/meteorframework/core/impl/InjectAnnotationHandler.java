package com.googlecode.meteorframework.core.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.annotation.InjectionAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ModelElement;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;
import com.googlecode.meteorframework.utils.Logging;

/**
 * Implements @Service injection.
 * @author Ted Stockwell
 */
@ProcessesAnnotations(Inject.class)
@SuppressWarnings("unchecked")
@ModelElement public class InjectAnnotationHandler implements InjectionAnnotationHandler {
	
	protected WeakReference<Scope> _scopeReference;
	protected WeakReference<Class> _javaType= null;
	protected WeakReference<Field> _field= null;
	
	@Override
	public boolean initialize(Scope scope, Object annotation, Object target, int parameterIndex)
	{
		_scopeReference= new WeakReference<Scope>(scope);
		
		boolean isSupported= false;
		if (target instanceof Field) {
			_field= new WeakReference<Field>((Field)target);
			_javaType= new WeakReference<Class>(((Field)target).getType());
			isSupported= true;
		}
		else if (target instanceof Method && 0 <= parameterIndex) {
			Class<?>[] types= ((Method)target).getParameterTypes();
			if (parameterIndex < types.length) {
				_javaType= new WeakReference<Class>(types[parameterIndex]);
				isSupported= true;
			}
		}
		
		if (_javaType.get().isPrimitive()) {
			Logging.severe("Primitive types cannot be injected with @Service annotation:"+target);
			return false;
		}
		
		return isSupported;
	}
	@Override
	public void inject(Object decorator, Object delegate, BindingContext bindingContext)
	{
		Class javaType= _javaType.get();
		Object value= null;
		if (Scope.class.isAssignableFrom(javaType)) {
			value= _scopeReference.get();
		}
		else {
			value= _scopeReference.get().getInstance(javaType, bindingContext.toArray());
			if (value == null)
				throw new MeteorException("Error injecting value into field "+_field.get().getGenericType()+". Service not found:"+Meteor.getURIForClass(_javaType.get()));
		}
		try {
			_field.get().set(decorator, value);
		} 
		catch (Throwable e) {
			throw MeteorException.getMeteorException("Error injecting value into field "+_field.get().getGenericType(),e);
		}
	}
}
