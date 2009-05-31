package com.googlecode.meteorframework.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.meteorframework.BindingContext;
import com.googlecode.meteorframework.Interceptor;
import com.googlecode.meteorframework.InvocationContext;
import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.annotation.After;
import com.googlecode.meteorframework.annotation.Around;
import com.googlecode.meteorframework.annotation.Before;




/**
 * Encapsulates information about a property or method invocation.
 * An invocation context is always available inside a Meteor invocation by calling 
 * the Meteor.getInvocationContext method.
 * 
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
public class InvocationContextImpl implements InvocationContext {
	
	static final int BEFORE= 0;
	static final int AROUND= 1;
	static final int METHODS= 2;
	static final int AFTER= 3;
	
	
	
	static final Object[] NO_ARGS = new Object[0];
	
	private List<Object> _args;
	Resource _receiver;
	private Object _result;
	private String _methodURI;
	private Throwable _fault;
	
	private List<Interceptor> _interceptors;
	private int _index;
	private BindingContext _bindingContext;
	
	private int _state= BEFORE; 
	
	
	public InvocationContextImpl(String methodURI, Resource receiver, Object[] args, List<Interceptor> interceptors, BindingContext bindingContext) {
		_receiver= receiver;
		ObjectImpl impl= ObjectImpl.getObjectImpl(_receiver);
		if (args == null || args.length <= 0) {
			_args= new ArrayList<Object>(0);			
		}
		else {
			_args= new ArrayList<Object>(args.length);
			for (Object object : args)
				_args.add(object);
		}
		_methodURI= methodURI;
		_bindingContext= (bindingContext != null) ? bindingContext : BindingContext.EMPTY_BINDINGS;
		
		
		// create binding context and then remove interceptors that have any 
		// binding not in the current binding context.
		if (receiver != null)
			_bindingContext= _bindingContext.union((BindingContext)impl.getValue(MeteorNS.Resource.facets));
		if (Meteor.isMeteorInvocation())
			_bindingContext= _bindingContext.union(Meteor.getInvocationContext().getFacets());
		_interceptors= new ArrayList<Interceptor>();
		for (Interceptor interceptor : interceptors) {
			if (_bindingContext.isSuperSetOf(interceptor.getBindingContext()))
				_interceptors.add(interceptor);
		}
		
		_index= _interceptors.size() - 1;		
	}
	public InvocationContextImpl(String methodURI, Resource receiver, Object[] args, List<Interceptor> interceptors) {
		this(methodURI, receiver, args, interceptors, null);
	}
	
	public InvocationContextImpl(BindingContext bindingContext)
	{
		this(null, null, NO_ARGS, Collections.EMPTY_LIST, bindingContext);
	}

	public InvocationContextImpl()
	{
		this(null, null, NO_ARGS, Collections.EMPTY_LIST, null);
	}

	public Object proceed() throws IllegalStateException, MeteorInvocationException 
	{
		if (_state == BEFORE || _state == AFTER)
			return _result;
		
		while (0 <= --_index) {
			Interceptor interceptor= _interceptors.get(_index);
			Method method= interceptor.getHandlerMethod();
			if (method.isAnnotationPresent(Before.class))
				continue;
			if (method.isAnnotationPresent(After.class))
				continue;
			if (method.isAnnotationPresent(Around.class))
				continue;
			return interceptor.process(this);
		}
		
		return null;
	}

	public List<Object> getArguments() {
		return _args;
	}

	public Throwable getFault() {
		return _fault;
	}

	public String getMethodURI() {
		return _methodURI;
	}

	public Object getResult() {
		return _result;
	}

	public Resource getReceiver() {
		return _receiver;
	}

	public void setFault(Throwable fault) {
		_fault= fault;		
	}

	public void setResult(Object value) {
		_result= value;
	}

	public Object run() {
		Meteor.pushInvocationContext(this);
		try {
			runAllBeforeMethods();
			runAllMethods();
			runAllAfterMethods();
			return _result;
		}
		finally {
			Meteor.popInvocationContext();
		}
	}
	
	private void runAllMethods()
	{
		_state= METHODS;
		for (_index= _interceptors.size(); 0 < _index--;) {
			Interceptor interceptor= _interceptors.get(_index);
			Method method= interceptor.getHandlerMethod();
			if (method.isAnnotationPresent(Before.class))
				continue;
			if (method.isAnnotationPresent(After.class))
				continue;
			if (method.isAnnotationPresent(Around.class))
				continue;
			_result= _interceptors.get(_index).process(this);
			break;
		}
	}

	private void runAllBeforeMethods()
	{
		_state= BEFORE;
		for (Interceptor interceptor : _interceptors) {
			if (interceptor.getHandlerMethod().isAnnotationPresent(Before.class)) 
				interceptor.process(this);
		}
	}
	private void runAllAfterMethods()
	{
		_state= AFTER;
		for (Interceptor interceptor : _interceptors) {
			Method handlerMethod= interceptor.getHandlerMethod(); 
			if (handlerMethod.isAnnotationPresent(After.class)) { 
				Object result= interceptor.process(this);
				
				/*
				 * An @After method signals its intention to 
				 * modify the result by declaring a return type 
				 * other than void;
				 */
				if (!Void.TYPE.equals(handlerMethod.getReturnType())) 
					_result= result;
			}
		}
	}
	@Override public BindingContext getFacets()
	{
		return _bindingContext;
	}
}
