package com.googlecode.meteorframework.core.binding;

import com.googlecode.meteorframework.core.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class BindingNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.core.binding";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Formatted {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.binding.Formatted";

		// properties
		public static final String format = "meteor:com.googlecode.meteorframework.core.binding.Formatted.format";

	}

	public static interface Testing {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.binding.Testing";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
