package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.annotation.Metadata;

//do not remove this annotation, it is used for testing purposes 
@Model({@Metadata({CoreNS.Resource.description, BootstrapTests.TEST_VALUE})})
public interface AnnotationTestType extends Service
{
	
	public Resource getResource();
	
	@Decorator public static abstract class AnnotationTestTypeImpl 
	implements AnnotationTestType, Resource 
	{
		@Inject private Scope _repository;
		private Resource _resource;
		
		@Override public void postConstruct() {
			_resource= _repository.getInstance(Resource.class);
		}
		
		@Override public Resource getResource()
		{
			return _resource;
		}
	}

}
