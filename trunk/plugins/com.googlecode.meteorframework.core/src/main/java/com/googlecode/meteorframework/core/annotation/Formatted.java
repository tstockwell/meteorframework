package com.googlecode.meteorframework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.binding.BindingNS;


@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@EquivalentMetadata({
	CoreNS.Resource.bindingContext, "[", 
		CoreNS.Resource.type, BindingNS.Formatted.TYPE, ";",
		BindingNS.Formatted.format, "{$value}",
	"]"
})
public @interface Formatted 
{
	public String value();
	
}
