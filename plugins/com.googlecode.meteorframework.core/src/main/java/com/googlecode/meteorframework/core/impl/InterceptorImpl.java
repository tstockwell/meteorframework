package com.googlecode.meteorframework.core.impl;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.Interceptor;
import com.googlecode.meteorframework.core.InvocationContext;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.Namespace;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Bind;


public class InterceptorImpl implements Interceptor {
	
	private static final AtomicInteger __counter= new AtomicInteger();
	
	private java.lang.reflect.Method _handlerMethod;
	private int _parameterCount;
	private Integer _order;
	private Namespace _namespace;
	private boolean _isDefaultImplementation;
	private Scope _scope;

	private BindingContext _bindingContext;
	
	public InterceptorImpl(Scope scope, java.lang.reflect.Method handlerMethod, boolean isDefaultImplementation) {
		_scope= scope;
		_handlerMethod= handlerMethod;
		Class<?>[] parameterTypes= _handlerMethod.getParameterTypes();
		_parameterCount= parameterTypes.length;
		_order= __counter.incrementAndGet();
		_isDefaultImplementation= isDefaultImplementation;
	}
	
	@Override public String toString()
	{
		return super.toString()+": { _handlerMethod="+_handlerMethod+"}";
	}
	
	public java.lang.reflect.Method getHandlerMethod() {
		return _handlerMethod;
	}
	
	@Override public Object process(InvocationContext ctx)
	{
		try {
			Object decorator= DecoratorManager.getDecorator(this, ctx);
			//Object mixin= ObjectImpl.getObjectImpl(ctx._receiver).castTo(_handlerMethod.getDeclaringClass());
				
			Object[] args= new Object[_parameterCount];
			List<?> list= ctx.getArguments();
			final int listSize= list.size();
			for (int off= 0;off < args.length; off++) {
				args[off]= (off < listSize) ? list.get(off) : new Object[0]/*assuming that the missing arg is vararg param*/;
			}
			
			return _handlerMethod.invoke(decorator, args);
		} 
		catch (Throwable t) {
			throw MeteorInvocationException.getMeteorException("Error in method "+_handlerMethod.toGenericString(), t);
		}
	}

	public int compareTo(Interceptor p_o) {
		InterceptorImpl impl= (InterceptorImpl)p_o;
		
		if (_isDefaultImplementation) {
			if (!impl._isDefaultImplementation)
				return -1;
		}
		else if (impl._isDefaultImplementation)
			return 1;
		
		Class<?> class1= _handlerMethod.getDeclaringClass();
		Class<?> class2= impl._handlerMethod.getDeclaringClass();
		if (class1.equals(class2)) {
			int parameterCount1= _handlerMethod.getParameterTypes().length;
			int parameterCount2= impl._handlerMethod.getParameterTypes().length;
			if (parameterCount1 < parameterCount2)
				return -1;
			if (parameterCount2 < parameterCount1)
				return 1;
		}

		int i= getNamespace().compareTo(impl.getNamespace());
		if (i != 0)
			return i;
		
		return _order.compareTo(impl._order);
	}

	@Override public Namespace getNamespace() {
		if (_namespace == null) {
			_namespace= Meteor.getSystemScope().findNamespace(_handlerMethod.getDeclaringClass().getPackage());
		}
		return _namespace;
	}
	
	@Override public boolean equals(Object obj)
	{
		if (!(obj instanceof InterceptorImpl))
			return false;
		InterceptorImpl i= (InterceptorImpl)obj;
		return _handlerMethod.equals(i._handlerMethod);
	}

	@Override public BindingContext getBindingContext()
	{
		if (_bindingContext == null) {
			
			HashSet<BindingType> bindings= new HashSet<BindingType>();
			
			// look for binding annotations attached to method
			for (Annotation annotation : _handlerMethod.getAnnotations()) {
				if (annotation instanceof Bind) { 
					Class<? extends BindingType> facetClass= ((Bind)annotation).value();
					BindingType bindingType= _scope.getInstance(facetClass);
					bindings.add(bindingType);
				}
			}
			// look for binding annotations at class level
			for (Annotation annotation : _handlerMethod.getDeclaringClass().getAnnotations()) {
				if (annotation instanceof Bind) {
					Class<? extends BindingType> facetClass= ((Bind)annotation).value();
					BindingType bindingType= _scope.getInstance(facetClass);
					bindings.add(bindingType);
				}
			}

			_bindingContext= BindingContext.getBindingContext(bindings);
		}
		return _bindingContext;
	}
}
