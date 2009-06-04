package com.googlecode.meteorframework.core.annotation;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.Scope;

/**
 * Different repository implementations will need to implement custom handling 
 * of the injection annotations like @Service, @Decorates, @Lookup, etc.
 * Custom repository implementations can customize the processing of injection 
 * annotations by creating a custom implementation of this interface and 
 * registering the InjectionAnnotationHandler via the 
 * MeteorSystem.registerInjectionAnnotationHandler method.
 * 
 * @author Ted Stockwell
 */
public interface InjectionAnnotationHandler {
	
	public interface Constructor {
		InjectionAnnotationHandler createAnnotationHandler();
	}

	/**
	 * Initialize this handler with runtime information.
	 * @param repository This handler will service objects created by this repository.
	 * @param annotation The associated annotation that this handler will implement.
	 * @param target The Java object to which the annotation was attached.
	 * @param if the annotation was attached to a Method parameter then target 
	 * 	is set to the method and parameterIndex is >= 0, else parameterIndex < 0.
	 * 
	 * @return true if this handler supports the given repository and will 
	 * 		actually do any processing of the given target object
	 */
	public boolean initialize(Scope repository, Object annotation, Object target, int parameterIndex);

	/**
	 * Inject dependency.
	 */
	public void inject(Object decorator, Object delegate, BindingContext bindingContext);
}
