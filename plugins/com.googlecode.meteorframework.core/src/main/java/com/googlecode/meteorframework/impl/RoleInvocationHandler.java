package com.googlecode.meteorframework.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.annotation.Model;


/**
 * Handles methods call on Role object and translates them to method calls on 
 * the underlying Resource. 
 *
 */
public class RoleInvocationHandler 
implements MethodInterceptor 
{
	static final int GET= 0;
	static final int SET= 1;
	static final int INVOKE= 2;
	
	private static class InvocationInfo {
		String resourceURI;
		int type;
	}
	
	/**
	 * Maps meteor methods to java methods and keeps track of what role 
	 * classes have been introspected. 
	 */
	private static class RoleMethods {
		HashMap<java.lang.reflect.Method, InvocationInfo> invocationInfoByMethod= new HashMap<java.lang.reflect.Method, InvocationInfo>();
		ArrayList<Class<?>> roleClasses= new ArrayList<Class<?>>(); 
	}
	
	private static final Object[] EMPTY_ARGS= new Object[0];
	static Method __getObjectImplMethod= null;
	static {
		try {
			__getObjectImplMethod= MeteorProxy.class.getMethod("getObjectImpl", new Class[0]);
		}
		catch (Throwable t) {
			throw new MeteorException("Internal Error", t);
		}
	}
	

	ObjectImpl _resourceImpl;
	Class<?> _roleClass;
	ObjectImpl _scope;
	RoleMethods _roleMethods;
	
	
	static HashMap<String, RoleMethods> _roleMethodsByRepository= new HashMap<String, RoleMethods>();

	/**
	 * @param repository The repository that will handle the method invocations
	 * @param resourceImpl The base Resource.
	 * @param roles
	 * @param roleClass
	 */
	public RoleInvocationHandler(ObjectImpl scope, ObjectImpl resourceImpl, Class<?> roleClass) {
		_roleClass= roleClass;
		_resourceImpl= resourceImpl;
		_scope= scope;
		synchronized (_roleMethodsByRepository) {
			_roleMethods= _roleMethodsByRepository.get(scope.internalGetURI());
			if (_roleMethods == null) {
				_roleMethods= new RoleMethods();
				_roleMethodsByRepository.put(scope.internalGetURI(), _roleMethods);
			}
		}
		
		initMethods(roleClass);
	}

	
	private void initMethods(Class<?> roleClass) {
		if (_roleMethods.roleClasses.contains(roleClass))
			return; // already processed this class
		
		synchronized (_roleMethods) {
			
			Method[] methods= roleClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method= methods[i];

				if (MeteorAnnotationUtils.isMeteorMethod(method)) {
					Method overridenMethod= ReflectionUtils.getOverriddenMethod(method);
					InvocationInfo info= new InvocationInfo();
					info.resourceURI= Meteor.getURIForMethod((overridenMethod == null) ? method : overridenMethod);
					info.type= INVOKE;
					_roleMethods.invocationInfoByMethod.put(method, info);
				}
				else if (MeteorAnnotationUtils.isMeteorProperty(method)) {
					String propertyName= method.getName();
					if (propertyName.startsWith("set") || propertyName.startsWith("get")) {
						propertyName= propertyName.substring(3);
						propertyName= propertyName.substring(0, 1).toLowerCase()+propertyName.substring(1);
					}
					else if (propertyName.startsWith("is")) {
						propertyName= propertyName.substring(2);
						propertyName= propertyName.substring(0, 1).toLowerCase()+propertyName.substring(1);
					}
					

					InvocationInfo setInfo= new InvocationInfo();
					setInfo.resourceURI= Meteor.getURIForMethod(method);
					setInfo.type= Void.TYPE.equals(method.getReturnType()) ? SET : GET;
					_roleMethods.invocationInfoByMethod.put(method, setInfo);
				}
			}
			
			_roleMethods.roleClasses.add(roleClass);
		}
		
		// process superclasses too, if any
		for (Class<?> superClass : roleClass.getInterfaces()) {
			if (superClass.isAnnotationPresent(Model.class))
				initMethods(superClass);
		}
		
		return;
	}


//	public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] arguments)
//	throws Throwable 
//	{
//	}

	public Object intercept(Object meteorObject, Method method, Object[] arguments, MethodProxy p_arg3) throws Throwable {
		if (arguments == null)
			arguments= EMPTY_ARGS;
		
		if (method.equals(__getObjectImplMethod))
			return _resourceImpl;
		
		InvocationInfo invocationInfo= _roleMethods.invocationInfoByMethod.get(method);
		if (invocationInfo != null) {
			switch (invocationInfo.type) {
			case GET:
				
				// need to add the property URI from the annotation to the args list
				Object[] args= new Object[2];
				args[0]= invocationInfo.resourceURI;
				args[1]= arguments;
				
				return MethodDispatcher.invoke(MeteorNS.Resource.getProperty, (Resource)meteorObject, args);

			case SET:
				
				// need to add the property URI from the annotation to the args list
				args= new Object[3];
				args[0]= invocationInfo.resourceURI;
				args[1]= arguments[0]; 
				args[2]= new Object[arguments.length - 1];
				System.arraycopy(arguments, 1, args[2], 0, arguments.length - 1);
				
				return MethodDispatcher.invoke(MeteorNS.Resource.setProperty, (Resource)meteorObject, args);
				
			case INVOKE:
				
//				// Need to add the target to the args list so that it matches 
//				// the args list on the associated handler method.
//				args= new Object[arguments.length+1];
//				args[0]= _resourceImpl;
//				System.arraycopy(arguments, 0, args, 1, arguments.length);
				
				return MethodDispatcher.invoke(invocationInfo.resourceURI, (Resource)meteorObject, arguments);

			default:
				throw new MeteorException("internal error");
			}
		}
		
		/*
		 * If the method is not a method declared by the resource class then 
		 * it cannot be supported.
		 * Actually, it's probably a method with a missing annotation or a misspelled URI.
		 */
		if (method.getDeclaringClass().isAssignableFrom(_resourceImpl.getClass()) == false) {
			throw new MeteorException("Unsupported method:"+method+" (maybe a method override and the base method is missing @Model annotation).");
		}
		
		// Invoked method is a regular Java method (like toString, equals), not a Model method...
		return method.invoke(_resourceImpl, arguments);
	}

}
