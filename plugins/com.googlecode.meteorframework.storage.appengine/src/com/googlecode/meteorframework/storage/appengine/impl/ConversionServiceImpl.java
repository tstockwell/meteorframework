package com.googlecode.meteorframework.storage.appengine.impl;

import java.util.Map;

import com.google.appengine.api.datastore.Entity;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.utils.ConversionService;

@Decorator
abstract public class ConversionServiceImpl
implements ConversionService
{
	@Inject Scope _scope;
	
	public Entity convert(Resource resource, Class<Entity> type) {
		Entity entity= new Entity(resource.getType().getURI());
		for (Property<?> property : resource.getContainedProperties())
		{
			Object value= resource.getProperty(property);
			entity.setProperty(property.getURI(), value);
		}
		return entity;
	}
	
	public Resource convert(Entity entity, Class<Resource> type) {
		Resource resource= _scope.createInstance(Resource.class);
		Map<String, Object> properties= entity.getProperties();
		for (String property: properties.keySet())
		{
			resource.setProperty(property, properties.get(property));
		}
		return resource;
	}
}
