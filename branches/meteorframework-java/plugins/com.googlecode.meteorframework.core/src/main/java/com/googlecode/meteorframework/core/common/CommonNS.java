package com.googlecode.meteorframework.core.common;

import com.googlecode.meteorframework.core.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class CommonNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.core.common";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Person {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.common.Person";

		// properties
		public static final String firstName = "meteor:com.googlecode.meteorframework.core.common.Person.firstName";
		public static final String lastName = "meteor:com.googlecode.meteorframework.core.common.Person.lastName";

	}

	public static interface Category {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.common.Category";

		// properties
		public static final String children = "meteor:com.googlecode.meteorframework.core.common.Category.children";
		public static final String parent = "meteor:com.googlecode.meteorframework.core.common.Category.parent";
		public static final String range = "meteor:com.googlecode.meteorframework.core.common.Category.range";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
