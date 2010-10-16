package com.googlecode.meteorframework.test.extension;

import com.googlecode.meteorframework.core.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class ExtensionNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.test.extension";

	public static final String BUNDLE= "com.googlecode.meteorframework.test";

	public static interface CustomerExtension {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.extension.CustomerExtension";

		// properties
		public static final String somePropertyWithADefaultValue = "meteor:com.googlecode.meteorframework.test.extension.CustomerExtension.somePropertyWithADefaultValue";
		public static final String taxId = "meteor:com.googlecode.meteorframework.test.extension.CustomerExtension.taxId";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
