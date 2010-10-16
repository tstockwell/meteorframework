package com.googlecode.meteorframework.core;

import java.util.Set;


/**
 * A decorator contributes methods that are woven into the call 
 * stacks of the method's associated functions.
 * 
 * @author Ted Stockwell
 */
@com.googlecode.meteorframework.core.annotation.ModelElement
public interface Decorator
extends ModelElement
{
	Set<Type<?>> getDecoratedTypes();
	
	Set<Method<?>> getMethods();
}
