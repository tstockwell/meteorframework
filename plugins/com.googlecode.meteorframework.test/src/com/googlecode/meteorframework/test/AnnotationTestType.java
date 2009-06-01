package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Resource;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.Service;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.annotation.Model;
import com.googlecode.meteorframework.annotation.Setting;

//do not remove this annotation, it is used for testing purposes 
@Model({@Setting(property=MeteorNS.Resource.description, value=BootstrapTests.TEST_VALUE)})
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
