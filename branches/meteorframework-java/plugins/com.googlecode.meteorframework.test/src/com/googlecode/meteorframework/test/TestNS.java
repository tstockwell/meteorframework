package com.googlecode.meteorframework.test;

import com.googlecode.meteorframework.core.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class TestNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.test";

	public static final String BUNDLE= "com.googlecode.meteorframework.test";

	public static interface CustomerStatement {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.CustomerStatement";

		// properties
		public static final String customer = "meteor:com.googlecode.meteorframework.test.CustomerStatement.customer";
		public static final String outstandingAmount = "meteor:com.googlecode.meteorframework.test.CustomerStatement.outstandingAmount";
		public static final String title = "meteor:com.googlecode.meteorframework.test.CustomerStatement.title";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.test.CustomerStatement$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.test.CustomerStatement$Constructor.create";

		}

	}

	public static interface AnnotationTestType {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.AnnotationTestType";

		// properties
		public static final String resource = "meteor:com.googlecode.meteorframework.test.AnnotationTestType.resource";

	}

	public static interface CustomerSubclass {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.CustomerSubclass";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.test.CustomerSubclass$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.test.CustomerSubclass$Constructor.create";

		}

	}

	public static interface Customer {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.Customer";

		// properties
		public static final String name = "meteor:com.googlecode.meteorframework.test.Customer.name";
		public static final String propertyWithADefaultValue = "meteor:com.googlecode.meteorframework.test.Customer.propertyWithADefaultValue";
		public static final String propertyWithNoSetMethod = "meteor:com.googlecode.meteorframework.test.Customer.propertyWithNoSetMethod";
		public static final String writeOnceProperty = "meteor:com.googlecode.meteorframework.test.Customer.writeOnceProperty";

		// methods
		public static final String someMethod = "meteor:com.googlecode.meteorframework.test.Customer.someMethod";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.test.Customer$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.test.Customer$Constructor.create";

		}

	}

	public static interface SomeScopedType {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.SomeScopedType";

	}

	public static interface SomeConfiguration {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.SomeConfiguration";

		// properties
		public static final String someProperty = "meteor:com.googlecode.meteorframework.test.SomeConfiguration.someProperty";

		// methods
		public static final String doSomething = "meteor:com.googlecode.meteorframework.test.SomeConfiguration.doSomething";

	}

	public static interface SomeSingleton {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.SomeSingleton";

	}

	public static interface SomeBindingType {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.test.SomeBindingType";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
