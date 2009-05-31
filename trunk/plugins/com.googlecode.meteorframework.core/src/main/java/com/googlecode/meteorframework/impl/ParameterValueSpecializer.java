package com.googlecode.meteorframework.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.meteorframework.Interceptor;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.annotation.MeteorAnnotationUtils;


/**
 * Selects a set of interceptors to handle a method invocation based 
 * on the value(s) of the arguments passed in the method invocation.
 * 
 * Currently this class only considers the value of the first argument to a 
 * method call and the type of the first argument must be a String or a Resource.
 * If the first argument is Resource then the value is checked against the 
 * resource's URI.
 * Future enhancements could include expanding the number, position, and 
 * supported types of the considered arguments. 
 *  
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
public class ParameterValueSpecializer extends BaseSpecializer {
	
	
	private static class SpecializerInfo {
		ParameterTypeSpecializer parameterTypeSpecializer= new ParameterTypeSpecializer();
		int lastWildInterceptorCount= 0; 
	}
	
	private static class InterceptorDescriptor {
		String argumentValue;
		Interceptor interceptor;
	}
	
	private HashMap<String, HashMap<String, SpecializerInfo>> _specializersByType= new HashMap<String, HashMap<String, SpecializerInfo>>();
	private HashMap<String, ArrayList<InterceptorDescriptor>> _wildInterceptorsByType= new HashMap<String, ArrayList<InterceptorDescriptor>>();
	
	public ParameterValueSpecializer() {
	}

	synchronized public void addInterceptor(String typeURI, String argumentValue, Interceptor p_interceptor) {
		
		InterceptorDescriptor interceptorDescriptor= new InterceptorDescriptor();
		interceptorDescriptor.argumentValue= argumentValue;
		interceptorDescriptor.interceptor= p_interceptor;
		
		if (MeteorAnnotationUtils.isWild(argumentValue)) {
			ArrayList<InterceptorDescriptor> wildInterceptors= _wildInterceptorsByType.get(typeURI);
			if (wildInterceptors == null) {
				wildInterceptors= new ArrayList<InterceptorDescriptor>();
				_wildInterceptorsByType.put(typeURI, wildInterceptors);
			}
			wildInterceptors.add(interceptorDescriptor);
		}
		else {
			HashMap<String, SpecializerInfo> specializersByValue= _specializersByType.get(typeURI);
			if (specializersByValue == null) {
				specializersByValue= new HashMap<String, SpecializerInfo>();
				_specializersByType.put(typeURI, specializersByValue);
			}
			SpecializerInfo specializerInfo= specializersByValue.get(argumentValue);
			if (specializerInfo == null) {
				specializerInfo= new SpecializerInfo();
				specializersByValue.put(argumentValue, specializerInfo);
			}
			specializerInfo.parameterTypeSpecializer.addInterceptor(interceptorDescriptor.interceptor);
		}
	}
	
	public static final String getArgumentValue(Object typeArgument) {

		if (typeArgument instanceof String) 
			return (String)typeArgument;

		if (typeArgument instanceof Resource) 
			return ObjectImpl.getObjectImpl(typeArgument).internalGetURI();

		if (typeArgument != null)
			return typeArgument.toString();
		
		return null;
	}
	
	synchronized public List<Interceptor> findInterceptors(String methodURI, Resource receiver, Object[] arguments) {
		
		String argumentValue= getArgumentValue(arguments[0]);
		
		/**
		 * First lookup specializers in Type associated with receiver, if any.
		 * If none found then move up the Type hierarchy.
		 */
		ObjectImpl impl= ObjectImpl.getObjectImpl(receiver);
		SpecializerInfo specializerInfo= null;
		String implementingType= null; // the type 
		List<String> allTypeURIs= impl.getAllMeteorTypeURIs();
		for (Iterator<String> i= allTypeURIs.iterator(); specializerInfo == null && i.hasNext();) 
		{
			implementingType= i.next();
			
			Map<String, SpecializerInfo> specializersByValue= _specializersByType.get(implementingType);
			if (specializersByValue != null)
				specializerInfo= specializersByValue.get(argumentValue);
		}
		
		/*
		 * All Meteor objects inherit com.googlecode.meteorframework.Resource even if Type does 
		 * not explicitly extend from Resource.
		 * So, if we have not yet found specializer then check Resource type.  
		 */
		if (specializerInfo == null) {
			Map<String, SpecializerInfo> specializersByValue= _specializersByType.get(implementingType= MeteorNS.Resource.TYPE);
			if (specializersByValue != null)
				specializerInfo= specializersByValue.get(argumentValue);
		}
		
		/*
		 * Check 'wild' interceptors to see if one would match this invocation, 
		 * if so then create specialize info.  
		 */
		if (specializerInfo == null) {
			for (Iterator<String> u= allTypeURIs.iterator(); specializerInfo == null && u.hasNext();) 
			{
				implementingType= u.next();
				ArrayList<InterceptorDescriptor> wildInterceptors= _wildInterceptorsByType.get(implementingType);
				if (wildInterceptors == null || wildInterceptors.size() <= 0)
					continue;
				for (int i= 0; specializerInfo == null && i < wildInterceptors.size(); i++) {
					InterceptorDescriptor interceptorDescriptor= wildInterceptors.get(i);
					if (MeteorAnnotationUtils.matches(interceptorDescriptor.argumentValue, argumentValue)) {
						HashMap<String, SpecializerInfo> specializersByValue= _specializersByType.get(implementingType);
						if (specializersByValue == null) {
							specializersByValue= new HashMap<String, SpecializerInfo>();
							_specializersByType.put(implementingType, specializersByValue);
						}
						specializerInfo= specializersByValue.get(argumentValue);
						if (specializerInfo == null) {
							specializerInfo= new SpecializerInfo();
							specializersByValue.put(argumentValue, specializerInfo);
						}
					}
				}
			}
		}
		
		/*
		 * If any 'wild' interceptors have been added since this this type was last accessed then 
		 * check the new wild interceptors to see if they match.  
		 */
		if (specializerInfo != null) {
			ArrayList<InterceptorDescriptor> wildInterceptors= _wildInterceptorsByType.get(implementingType);
			if (wildInterceptors != null && 0 < wildInterceptors.size()) {
				if (specializerInfo.lastWildInterceptorCount < wildInterceptors.size()) {
					for (int i= specializerInfo.lastWildInterceptorCount; i < wildInterceptors.size(); i++) {
						InterceptorDescriptor interceptorDescriptor= wildInterceptors.get(i);
						if (MeteorAnnotationUtils.matches(interceptorDescriptor.argumentValue, argumentValue))  
							specializerInfo.parameterTypeSpecializer.addInterceptor(interceptorDescriptor.interceptor);
					}
					specializerInfo.lastWildInterceptorCount= wildInterceptors.size();
				}
			}
		}

		if (specializerInfo == null)
			return Collections.EMPTY_LIST;
		
		// constructor & property overloads don't include the 1st argument (the 
		// argument that specifies the type or the property) so strip that out.
		Object[] args= new Object[arguments.length - 1]; 
		System.arraycopy(arguments, 1, args, 0, arguments.length - 1);
		
//		/*
//		 * Property overloads include the target as a parameter so if the given 
//		 * target is not null then add that to the args list as the 1st argument.
//		 * Constructor overloads do NOT include the target as a parameter. 
//		 */
//		if (target != null) {
//			Object[] objects= new Object[args.length + 1]; 
//			objects[0]= target; 
//			System.arraycopy(args, 0, objects, 1, args.length);
//			args= objects;
//		}
		
		return specializerInfo.parameterTypeSpecializer.findInterceptors(methodURI, receiver, args);
		
	}

}
