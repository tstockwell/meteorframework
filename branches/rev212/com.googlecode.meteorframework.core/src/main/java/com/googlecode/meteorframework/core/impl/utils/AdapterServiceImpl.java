package com.googlecode.meteorframework.core.impl.utils;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.TypeLiteral;
import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.utils.UtilsNS;
import com.googlecode.meteorframework.core.utils.Adapter;
import com.googlecode.meteorframework.core.utils.AdapterService;

@Decorator 
public abstract class AdapterServiceImpl 
implements AdapterService 
{
	@Inject Scope _scope;
	
	@Override
	public <T> T adapt(Object object, TypeLiteral<T> targetType)
	{
		return _scope.getInstance(targetType.getRawType());
	}
	
	@Override
	public <T> T adapt(Object object, Class<T> targetType)
	{
		return _scope.getInstance(targetType);
	}
	
	public <T extends Adapter> T adapt(Object object, TypeLiteral<T> targetType)
	{
		T adapter= _scope.getInstance(targetType.getRawType());
		((Resource)adapter).setProperty(UtilsNS.Adapter.adaptedObject, object);
		return adapter;
	}
	
	public <T extends Adapter> T adapt(Object object, Class<T> targetType)
	{
		T adapter= _scope.getInstance(targetType);
		((Resource)adapter).setProperty(UtilsNS.Adapter.adaptedObject, object);
		return adapter;
	}
}
