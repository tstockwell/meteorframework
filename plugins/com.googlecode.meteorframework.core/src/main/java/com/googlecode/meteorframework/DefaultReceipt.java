package com.googlecode.meteorframework;

import java.util.concurrent.TimeUnit;


public class DefaultReceipt<V> implements Receipt<V> {
	
	V _result;
	
	public DefaultReceipt(V result) {
		_result= result;
	}

	@Override public V getResult() {
		return _result;
	}

	@Override public V getResult(long timeout, TimeUnit unit)  
	{
		return _result;
	}

	@Override public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override public boolean isCancelled() {
		return false;
	}

	@Override public boolean isDone() {
		return true;
	}

	@Override public V get() 
	{
		return getResult();
	}

	@Override public V get(long timeout, TimeUnit unit) 
	{
		return getResult(timeout, unit);
	}

}
