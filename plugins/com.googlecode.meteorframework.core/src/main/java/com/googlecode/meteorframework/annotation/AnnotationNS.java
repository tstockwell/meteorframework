package com.googlecode.meteorframework.annotation;

import com.googlecode.meteorframework.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Generated;

@Generated public class AnnotationNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.annotation";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Bind {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.annotation.Bind";

		// methods
		public static final String value = "meteor:com.googlecode.meteorframework.annotation.Bind.value";

	}



	public void contributeMetaData(com.googlecode.meteorframework.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
