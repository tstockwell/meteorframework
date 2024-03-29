package com.googlecode.meteorframework.core.utils;

import com.googlecode.meteorframework.core.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class UtilsNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.core.utils";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Configuration {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.Configuration";

	}

	public static interface Adapter {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.Adapter";

		// properties
		public static final String adaptedObject = "meteor:com.googlecode.meteorframework.core.utils.Adapter.adaptedObject";

	}

	public static interface RDFDocumentBuilder {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.RDFDocumentBuilder";

		// properties
		public static final String document = "meteor:com.googlecode.meteorframework.core.utils.RDFDocumentBuilder.document";

		// methods
		public static final String addResource = "meteor:com.googlecode.meteorframework.core.utils.RDFDocumentBuilder.addResource";
		public static final String createRootElement = "meteor:com.googlecode.meteorframework.core.utils.RDFDocumentBuilder.createRootElement";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.RDFDocumentBuilder$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.utils.RDFDocumentBuilder$Constructor.create";

		}

	}

	public static interface OrderHintProvider {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.OrderHintProvider";

		// properties
		public static final String after = "meteor:com.googlecode.meteorframework.core.utils.OrderHintProvider.after";
		public static final String before = "meteor:com.googlecode.meteorframework.core.utils.OrderHintProvider.before";

	}

	public static interface AdapterService {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.AdapterService";

		// methods
		public static final String adapt = "meteor:com.googlecode.meteorframework.core.utils.AdapterService.adapt";

	}

	public static interface ConversionService {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.ConversionService";

		// methods
		public static final String convert = "meteor:com.googlecode.meteorframework.core.utils.ConversionService.convert";

	}

	public static interface Messages {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.utils.Messages";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
