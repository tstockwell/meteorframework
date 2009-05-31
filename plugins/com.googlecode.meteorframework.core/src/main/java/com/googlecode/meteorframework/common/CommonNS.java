package com.googlecode.meteorframework.common;

import com.googlecode.meteorframework.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Generated;

@Generated public class CommonNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.common";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Category {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.common.Category";

		// properties
		public static final String children = "meteor:com.googlecode.meteorframework.common.Category.children";
		public static final String parent = "meteor:com.googlecode.meteorframework.common.Category.parent";
		public static final String range = "meteor:com.googlecode.meteorframework.common.Category.range";

	}

	public static interface Person {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.common.Person";

		// properties
		public static final String firstName = "meteor:com.googlecode.meteorframework.common.Person.firstName";
		public static final String lastName = "meteor:com.googlecode.meteorframework.common.Person.lastName";

	}



	public void contributeMetaData(com.googlecode.meteorframework.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
