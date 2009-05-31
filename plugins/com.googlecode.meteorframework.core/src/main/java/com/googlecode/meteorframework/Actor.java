package com.googlecode.meteorframework;

import com.googlecode.meteorframework.annotation.Model;

@Model public interface Actor {
	
	public <T> Receipt<T> send(Message<T> message);
	
	void exit();
		
}
