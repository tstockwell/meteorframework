package com.googlecode.meteorframework.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.MeteorMethodNotImplementedException;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Method;
import com.googlecode.meteorframework.core.Resource;



/**
 * Implements Meteor method dispatch.
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
public class MethodDispatcher {
	
	static private Map<String, Map<String, ParameterTypeSpecializer>> _specializersByType= new HashMap<String, Map<String,ParameterTypeSpecializer>>();
	

	public static <T> T invoke(Method<T> p_method, Resource receiver, Object... arguments) {
		return (T)invoke(ObjectImpl.getObjectImpl(p_method).internalGetURI(), receiver, arguments);
	}
	public static Object invoke(String methodURI, Resource receiver, Object... arguments) {
		/*
		 *	This method works by finding the appropriate set interceptors and 
		 *	then invoking the first interceptor.
		 */
		
		ArrayList<Interceptor> interceptors= new ArrayList<Interceptor>();
		
		MethodSpecializer methodSpecializer= null;
		ObjectImpl impl= ObjectImpl.getObjectImpl(receiver);		
		
		/**
		 * First lookup specializers for method in receiver iteself (that is look for 
		 * decorators added directly to the object, if any.
		 */
		List<String> allTypes= impl.getAllMeteorTypeURIs();
		for (Iterator<String> i= allTypes.iterator(); methodSpecializer == null && i.hasNext();) 
		{
			String typeURI= i.next();
			if ((methodSpecializer= impl.findMethodSpecializer(typeURI, methodURI)) != null)
				interceptors.addAll(0, methodSpecializer.findInterceptors(methodURI, impl, arguments));
		}
		
		/**
		 * lookup specializers for method in Type associated with receiver, if any.
		 * Move up the Type hierarchy.
		 */
		for (Iterator<String> i= allTypes.iterator(); i.hasNext();) 
		{
			String typeURI= i.next();
			Map<String, ParameterTypeSpecializer> specializersByMethod= _specializersByType.get(typeURI);
			if (specializersByMethod != null)
				if ((methodSpecializer= specializersByMethod.get(methodURI)) != null)
					interceptors.addAll(0, methodSpecializer.findInterceptors(methodURI, impl, arguments));
		}
		
		/*
		 * All Meteor objects inherit com.googlecode.meteorframework.Resource even if Type does 
		 * not explicitly extend from Resource.
		 * So, add interceptors from Resource type.  
		 */
		Map<String, ParameterTypeSpecializer> specializersByMethod= _specializersByType.get(MeteorNS.Resource.TYPE);
		if (specializersByMethod != null)
			if ((methodSpecializer= specializersByMethod.get(methodURI)) != null)
				interceptors.addAll(0, methodSpecializer.findInterceptors(methodURI, impl, arguments));
		
		if (interceptors.isEmpty())
			throw new MeteorMethodNotImplementedException(methodURI);

		InvocationContextImpl ctx= 
			new InvocationContextImpl(methodURI, receiver, arguments, interceptors);

		return ctx.run();
	}

	static public ParameterTypeSpecializer getMethodSpecializer(String typeURI, String p_methodURI) {
		Map<String, ParameterTypeSpecializer> specializersByMethod= _specializersByType.get(typeURI);
		if (specializersByMethod == null) {
			specializersByMethod= new HashMap<String, ParameterTypeSpecializer>();
			_specializersByType.put(typeURI, specializersByMethod);
		}
		
		ParameterTypeSpecializer specializer= specializersByMethod.get(p_methodURI);
		if (specializer == null) {
			specializer= new ParameterTypeSpecializer();
			specializersByMethod.put(p_methodURI, specializer);
		}

		return specializer;
	}

}
