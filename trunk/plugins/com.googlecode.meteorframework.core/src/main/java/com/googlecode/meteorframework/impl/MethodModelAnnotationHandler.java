package com.googlecode.meteorframework.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.meteorframework.Interceptor;
import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.After;
import com.googlecode.meteorframework.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.annotation.ProcessesAnnotations;


/**
 * Processes @Model annotation attached to Java method declarations.
 * Only processes java methods that represent Meteor methods.
 * @see PropertyModelAnnotationHandler 
 * @author Ted Stockwell
 */
@ProcessesAnnotations({Model.class})
@Model public class MethodModelAnnotationHandler implements ModelAnnotationHandler {
	
	private String[] _methodURIs;
	private String[] _typeURIs;
	private java.lang.reflect.Method _handlerMethod;
	private boolean _isDefaultImplementation= false;
	private Scope _scope;
	private Model _annotation;
	
	
	static class WildInfo { String wildURI; String typeURI; Interceptor interceptor; };
	static ArrayList<WildInfo> __wildInfo= new ArrayList<WildInfo>();
	public static abstract class WildInterceptorCreator implements Scope 
	{
		@Override @After public void addResource(Resource resource)
		{
			ObjectImpl impl= ObjectImpl.getObjectImpl(resource);
			if (impl.getTypeURI().equals(MeteorNS.Method.TYPE)) {
				String methodURI= impl.internalGetURI();
				for (WildInfo info : __wildInfo) {
					if (MeteorAnnotationUtils.matches(info.wildURI, methodURI)) {
						addInterceptor(info.interceptor, info.typeURI, methodURI);
						break;
					}
				}
			}
		}
	}
	static HashMap<Scope, WildInterceptorCreator> __decorators= new HashMap<Scope, WildInterceptorCreator>(); 
	
	@Override public boolean initialize(Scope scope, Object annotation, Object target) {
		_scope= scope;
		
		if (__decorators.get(scope) == null) {
			ObjectImpl impl= ObjectImpl.getObjectImpl(_scope);
			WildInterceptorCreator decorator= impl.addDecorator(WildInterceptorCreator.class);
			__decorators.put(_scope, decorator);
		}
		
		
		if (!(target instanceof Method))
			return false;		
		_handlerMethod= (Method)target;
		_annotation= (Model)annotation;
		if (!Modifier.isAbstract(_handlerMethod.getModifiers())) {
			List<String> advises= MeteorAnnotationUtils.getPropertyValues(_annotation, MeteorNS.Method.advises);
			if (advises.isEmpty()) {
				
				Class<?> class1= _handlerMethod.getDeclaringClass();
				_typeURIs= new String[] { Meteor.getURIForClass(class1) };
				Method implementedMethod= _handlerMethod;
				
				// does method override another Model method?
				_isDefaultImplementation= true;
				try {
					Method overriddenMethod= ReflectionUtils.getOverriddenMethod(_handlerMethod);
					if (overriddenMethod != null) {
						implementedMethod= overriddenMethod;
						_isDefaultImplementation= false;
					}
				} 
				catch (Throwable e) { 
				}
				
				_methodURIs= new String[] { Meteor.getURIForMethod(implementedMethod) };
			}
			else {			
				_methodURIs= advises.toArray(new String[advises.size()]);
				_typeURIs= new String[advises.size()];
				String uri= Meteor.getURIForClass(_handlerMethod.getDeclaringClass().getSuperclass());
				for (int i= 0; i < _typeURIs.length; i++)
					_typeURIs[i]= uri;
			}
		}
		return true;
	}


	public void addBehavior() {
		if (!Modifier.isAbstract(_handlerMethod.getModifiers())) 
		{
			final Interceptor interceptor= new InterceptorImpl(_scope, _handlerMethod, _isDefaultImplementation); 

			for (int i= 0; i < _methodURIs.length; i++) {
				String methodURI= _methodURIs[i]; 
				if (MeteorAnnotationUtils.isWild(methodURI)) {
					WildInfo info= new WildInfo();
					info.wildURI= methodURI;
					info.typeURI= _typeURIs[i];
					info.interceptor= interceptor;
					__wildInfo.add(info);
				}
				else
					addInterceptor(interceptor, _typeURIs[i], methodURI);
			}
		}
	}

	static void addInterceptor(Interceptor interceptor, String typeURI, String methodURI) 
	{
		ParameterTypeSpecializer specializer= (ParameterTypeSpecializer) 
			MethodDispatcher.getMethodSpecializer(typeURI, methodURI);

		// add interceptor to specializer
		specializer.addInterceptor(interceptor);
	}

	public void addDerivedMetadata() {
		// do nothing		
	}


	@Override public void addAnnotationMetadata()
	{
		Map<String, List<String>> annotationProperties= MeteorAnnotationUtils.getAllPropertyValues(_annotation);
		if (!annotationProperties.isEmpty()) {
			Method implementedMethod= ReflectionUtils.getOverriddenMethod(_handlerMethod);
			if (implementedMethod == null) 
				implementedMethod= _handlerMethod;
			String resourceURI= Meteor.getURIForMethod(implementedMethod);
			
			Resource resource= RepositoryImpl.findResourceByURI(_scope, resourceURI);
			for (Object propertyURI : annotationProperties.keySet()) {
				List<String> values= annotationProperties.get(propertyURI);
				for (String value : values)
					resource.setProperty((String)propertyURI, value);
			}
		}
	}


	@Override public void addTypeDefinitions()
	{
		// TODO Auto-generated method stub
		
	}
}
