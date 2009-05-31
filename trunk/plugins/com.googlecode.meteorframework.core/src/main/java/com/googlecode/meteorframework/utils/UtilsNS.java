package com.googlecode.meteorframework.utils;

import com.googlecode.meteorframework.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Generated;

@Generated public class UtilsNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.utils";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface OrderHintProvider {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.OrderHintProvider";

		// properties
		public static final String after = "meteor:com.googlecode.meteorframework.utils.OrderHintProvider.after";
		public static final String before = "meteor:com.googlecode.meteorframework.utils.OrderHintProvider.before";

	}

	public static interface Messages {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.Messages";

		// methods
		public static final String getText = "meteor:com.googlecode.meteorframework.utils.Messages.getText";

	}

	public static interface AdapterService {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.AdapterService";

		// methods
		public static final String adapt = "meteor:com.googlecode.meteorframework.utils.AdapterService.adapt";

	}

	public static interface ConversionService {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.ConversionService";

		// methods
		public static final String convert = "meteor:com.googlecode.meteorframework.utils.ConversionService.convert";

	}

	public static interface Configuration {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.Configuration";

	}

	public static interface RDFDocumentBuilder {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.RDFDocumentBuilder";

		// properties
		public static final String document = "meteor:com.googlecode.meteorframework.utils.RDFDocumentBuilder.document";

		// methods
		public static final String addResource = "meteor:com.googlecode.meteorframework.utils.RDFDocumentBuilder.addResource";
		public static final String createRootElement = "meteor:com.googlecode.meteorframework.utils.RDFDocumentBuilder.createRootElement";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.RDFDocumentBuilder$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.utils.RDFDocumentBuilder$Constructor.create";

		}

	}

	public static interface Adapter {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.utils.Adapter";

		// properties
		public static final String adaptedObject = "meteor:com.googlecode.meteorframework.utils.Adapter.adaptedObject";

	}



	public void contributeMetaData(com.googlecode.meteorframework.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
