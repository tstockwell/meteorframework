package com.googlecode.meteorframework.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.MeteorException;


/**
 * A method specializer determines what interceptors are applicable 
 * to a particular method invocation.
 *  
 * @author ted stockwell
 */
@SuppressWarnings("unchecked")
abstract public class BaseSpecializer implements MethodSpecializer {
	
	static final HashMap<Class, Class> _objectTypesByPrimitiveTypes= new HashMap<Class, Class>();
	static {
		_objectTypesByPrimitiveTypes.put(Boolean.TYPE, Boolean.class);
		_objectTypesByPrimitiveTypes.put(Character.TYPE, Character.class);
		_objectTypesByPrimitiveTypes.put(Byte.TYPE, Byte.class);
		_objectTypesByPrimitiveTypes.put(Short.TYPE, Short.class);
		_objectTypesByPrimitiveTypes.put(Integer.TYPE, Integer.class);
		_objectTypesByPrimitiveTypes.put(Long.TYPE, Long.class);
		_objectTypesByPrimitiveTypes.put(Float.TYPE, Float.class);
		_objectTypesByPrimitiveTypes.put(Double.TYPE, Double.class);
		_objectTypesByPrimitiveTypes.put(Void.TYPE, Void.class);
	}
	
	private List<Interceptor> _interceptors= null;
	private boolean _isSorted= false;
	
	public List<Interceptor> getLocalInterceptors() {
		if (_interceptors == null)
			return Collections.EMPTY_LIST;
		if (!_isSorted) {
			Collections.sort(_interceptors);
			_isSorted= true;
		}
		return _interceptors;
	}
	
	public void addLocalInterceptor(Interceptor p_interceptor) {
		if (_interceptors == null) 
			_interceptors= new ArrayList<Interceptor>();
		if (_interceptors.contains(p_interceptor))
			throw new MeteorException("Internal Error: Duplicate interceptors found:"+p_interceptor);
		_interceptors.add(p_interceptor);
		_isSorted= false;
	}
	

	/**
	 * @param parameterType The compile-time type
	 * @param argumentType The type of the argument passed at runtime.
	 * @return TRUE if the runtime argument type matches the compile-time parameter type
	 */
	protected boolean parameterTypesMatch(Class<?> parameterType, Class argumentType) {
		
		// a null argument matches everything (since we don;t know what type it is)
		if (argumentType == null)
			return true;
		
		if (parameterType.isPrimitive()) 
			parameterType= _objectTypesByPrimitiveTypes.get(parameterType);
		if (argumentType.isPrimitive()) 
			argumentType= _objectTypesByPrimitiveTypes.get(argumentType);
		
		return parameterType.isAssignableFrom(argumentType);
	}
	
	
}
