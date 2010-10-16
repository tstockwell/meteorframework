package com.googlecode.meteorframework.core.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.Function;
import com.googlecode.meteorframework.core.Resource;


/**
 * Selects a set of interceptors to use to handle a method invocation based 
 * on the parameter types of the method that is being invoked.
 *  
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
public class ParameterTypeSpecializer extends BaseSpecializer {
	
	private static class SpecializerInfo {
		public SpecializerInfo(int parameterIndex) {
			parameterTypeSpecializer= new ParameterTypeSpecializer(parameterIndex);
		}
		ParameterTypeSpecializer parameterTypeSpecializer;
		int lastInterceptorCount= 0; 
	}
	
	
	class InterceptorDescriptor {
		Interceptor interceptor;
		
		public InterceptorDescriptor(Interceptor p_interceptor) {
			interceptor= p_interceptor;
		}

		/**
		 * @param argumentType The type of the argument passed at runtime.
		 * @return TRUE if the runtime argument type matches the compile-time parameter type
		 */
		public boolean matchesArg(Object arg) {
			
			
			// a null argument matches everything (since we don;t know what type it is)
			if (arg == null)
				return true;
			
			Class typeClass= interceptor.getHandlerMethod().getParameterTypes()[_parameterIndex];
			Class argumentType= arg.getClass();

			if (typeClass.isPrimitive()) 
				typeClass= _objectTypesByPrimitiveTypes.get(typeClass);
			if (argumentType.isPrimitive()) 
				argumentType= _objectTypesByPrimitiveTypes.get(argumentType);
			
			if (arg instanceof Class && Class.class.equals(typeClass)) {
				Type genericParameterType= interceptor.getHandlerMethod().getGenericParameterTypes()[_parameterIndex];
				if (genericParameterType instanceof ParameterizedType) {
					ParameterizedType parameterizedType= (ParameterizedType)genericParameterType;
					Type actualType= parameterizedType.getActualTypeArguments()[0];
					if (actualType instanceof Class) {
						return ((Class)actualType).isAssignableFrom((Class)arg);
					}
					else if (actualType instanceof WildcardType) {
						WildcardType wildcardType= (WildcardType)actualType;
						Type[] types= wildcardType.getLowerBounds();
						if (types == null || types.length <= 0)
							return true;
						for (Type type : types) {
							if (((Class)type).isAssignableFrom((Class)arg))
								return true;
						}
						return false;
					}
					else if (actualType instanceof TypeVariable) {
						TypeVariable typeVariable= (TypeVariable)actualType;
						Type[] types= typeVariable.getBounds();
						if (types == null || types.length <= 0)
							return true;
						for (Type type : types) {
							if (((Class)type).isAssignableFrom((Class)arg))
								return true;
						}
						return false;
					}
				}
			}
			
			return typeClass.isAssignableFrom(argumentType);
		}
	}
	
	ArrayList<InterceptorDescriptor> _interceptorDescriptors= new ArrayList<InterceptorDescriptor>();	
	HashMap<String, SpecializerInfo> _specializersByParameterType= new HashMap<String, SpecializerInfo>();
	int _parameterIndex;
	
	public ParameterTypeSpecializer() {
		this (0);
	}	
	/**
	 * @param parameterIndex The index of the first parameter to consider, 0 for first parameter, 1 for 2nd, etc
	 */
	public ParameterTypeSpecializer(int parameterIndex) {
		_parameterIndex= parameterIndex;
	}
	
	public List<Interceptor> findInterceptors(String methodURI, Resource target, Object[] arguments) 
	{
		
		List<Interceptor> currentMatching=  getLocalInterceptors();
		
		// if there are no arguments then return interceptors attached to this specializer
		if (arguments == null || arguments.length <= _parameterIndex)
			return currentMatching;
		
		Object arg= arguments[_parameterIndex];
		String typeName= getUniqueTypeIdentifier(arg);
		
		SpecializerInfo specializerInfo= null;
		synchronized (_specializersByParameterType) {
			specializerInfo= _specializersByParameterType.get(typeName);
			if (specializerInfo == null) {
				specializerInfo= new SpecializerInfo(_parameterIndex+1);
				_specializersByParameterType.put(typeName, specializerInfo);
			}
		}
		
		/*
		 * If any interceptors have been added since this this type was last accessed then 
		 * check the new interceptors to see if they match.  
		 */
		if (specializerInfo.lastInterceptorCount < _interceptorDescriptors.size()) {
			synchronized (specializerInfo) {
				for (int i= specializerInfo.lastInterceptorCount; i < _interceptorDescriptors.size(); i++) {
					InterceptorDescriptor interceptorDescriptor= _interceptorDescriptors.get(i);
					if (interceptorDescriptor.matchesArg(arg))
						specializerInfo.parameterTypeSpecializer.addInterceptor(interceptorDescriptor.interceptor);
				}
				specializerInfo.lastInterceptorCount= _interceptorDescriptors.size();
			}
		}		
		
		List<Interceptor> moreMatching= specializerInfo.parameterTypeSpecializer.findInterceptors(methodURI, target, arguments);
		if (currentMatching.isEmpty())
			return moreMatching;
		if (moreMatching.isEmpty())
			return currentMatching;

		ArrayList<Interceptor> allMatching= new ArrayList<Interceptor>();
		allMatching.addAll(currentMatching);
		allMatching.addAll(moreMatching);
		Collections.sort(allMatching);
		return allMatching;
	}
	
	/**
	 * Returns a unique id that represents all the Java types represented by a given object.
	 */
	static String getUniqueTypeIdentifier(Object arg) {
		if (arg == null) 
			return "null";
		
		String id= "";
		ObjectImpl impl= ObjectImpl.getObjectImpl(arg);
		if (impl != null) {
			com.googlecode.meteorframework.core.Type type= impl.getType();
			id= type.getJavaType().getName();
			
			Set<com.googlecode.meteorframework.core.Type> extensionTypes= type.getExtensions();
			if (extensionTypes != null)
				for (com.googlecode.meteorframework.core.Type xType : extensionTypes) {
					id+= ";"+xType.getJavaType().getName();
				}
		}
		else { 
			Class class1= arg.getClass();
			id= class1.getName();
			if (arg instanceof Class)
				id+= "<"+((Class)arg).getName()+">";
		}

		return id;
	}
	
	
	
	public void removeSpecializers(Function p_method) {
		_specializersByParameterType.remove(ObjectImpl.getObjectImpl(p_method).internalGetURI());
	}
	public void addInterceptor(Interceptor p_interceptor) {
		Class[] parameterTypes= p_interceptor.getHandlerMethod().getParameterTypes();
		
		// if there are no arguments then add interceptor to this specializer
		if (parameterTypes.length <= _parameterIndex) {
			addLocalInterceptor(p_interceptor);
			return;
		}
		
		_interceptorDescriptors.add(new InterceptorDescriptor(p_interceptor));
	}
	
}
