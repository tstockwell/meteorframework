package com.googlecode.meteorframework.core.annotation;

import com.googlecode.meteorframework.core.Scope;

/**
 * Different repository implementations will need to implement custom handling 
 * of the modeling annotations like @Model, @Decorator, @SemanticEquivalent, etc.
 * Custom repository implementations can customize the processing of modeling 
 * annotations by creating a custom implementation of this interface and 
 * registering the ModelAnnotationHandler via the MeteorSystem.registerModelAnnotationHandler  
 * method.
 * 
 * Models can be extended with metadata from various sources.
 * Every time a new model annotation is discovered in any of these sources 
 * a new ModelAnnotationHandler will be created (an ModelAnnotationHandler must have a non-arg 
 * constructor) and the ModelAnnotationHandler's initialize method will be passed 
 * the discovered annotation and a reference to the object to which the 
 * annotation was attached.    
 * Then every time a new repository is created the overloadBehavior method 
 * will be called and a reference to the new repository will be passed in.
 * If the ModelAnnotationHandler can process overloads for the given repository 
 * type then the ModelAnnotationHandler will then do whatever is neccessary to add 
 * the overload metadata to the given repository.  
 *   
 * @author Ted Stockwell
 *
 */
public interface ModelAnnotationHandler {
	
	public static interface Factory {
		ModelAnnotationHandler createAnnotationHandler();
	}

	/**
	 * Initialize this handler with runtime information.
	 * @param layer The model layer to which the associated overload belongs.
	 * @param annotation The associated annotation that this handler will implement.
	 * @param target The Java object to which the annotation was attached.
	 * 
	 * @return true if this handler supports the given repository and will 
	 * 		actually do any processing of the given target object
	 */
	boolean initialize(Scope repository, Object annotation, Object target);

	/**
	 * Add behavior to the given repository, that is, register all decorators 
	 * and interceptors with the repository. 
	 * Adding behavior is a repository specific operation.
	 * When loading a module of data, like an OSGi bundle, this method 
	 * should be invoked before any of the metadata in the module is loaded 
	 * in order to ensure correct behavior while loading the module.  
	 */
	void addBehavior();

	/**
	 * If the annotation target represents a Type, Method, or Property then 
	 * create a Meteor model element that represents the target and add the 
	 * model element to the repository.
	 */
	void addTypeDefinitions();

	
	/**
	 * Add additional metadata to a model element after the model element has 
	 * been created.
	 */
	void addAnnotationMetadata();

	/**
	 * Add any additional, derived metadata to the given repository.
	 * When loading a module of data, like an OSGi bundle, this method 
	 * should be invoked after all other metadata in the module is loaded 
	 * since this step most likely depends on having that information 
	 * already available. 
	 */
	void addDerivedMetadata();
}
