package com.googlecode.meteorframework.core.query;

import com.googlecode.meteorframework.core.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Generated;

@Generated public class QueryNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.core.query";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Operators {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Operators";

		// methods
		public static final String and = "meteor:com.googlecode.meteorframework.core.query.Operators.and";
		public static final String eq = "meteor:com.googlecode.meteorframework.core.query.Operators.eq";
		public static final String ge = "meteor:com.googlecode.meteorframework.core.query.Operators.ge";
		public static final String gt = "meteor:com.googlecode.meteorframework.core.query.Operators.gt";
		public static final String isNull = "meteor:com.googlecode.meteorframework.core.query.Operators.isNull";
		public static final String le = "meteor:com.googlecode.meteorframework.core.query.Operators.le";
		public static final String like = "meteor:com.googlecode.meteorframework.core.query.Operators.like";
		public static final String lt = "meteor:com.googlecode.meteorframework.core.query.Operators.lt";
		public static final String ne = "meteor:com.googlecode.meteorframework.core.query.Operators.ne";
		public static final String not = "meteor:com.googlecode.meteorframework.core.query.Operators.not";
		public static final String or = "meteor:com.googlecode.meteorframework.core.query.Operators.or";

	}

	public static interface InRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.InRestriction";

		// properties
		public static final String property = "meteor:com.googlecode.meteorframework.core.query.InRestriction.property";
		public static final String values = "meteor:com.googlecode.meteorframework.core.query.InRestriction.values";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.InRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.query.InRestriction$Constructor.create";

		}

	}

	public static interface Restrictions {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Restrictions";

		// methods
		public static final String and = "meteor:com.googlecode.meteorframework.core.query.Restrictions.and";
		public static final String not = "meteor:com.googlecode.meteorframework.core.query.Restrictions.not";
		public static final String or = "meteor:com.googlecode.meteorframework.core.query.Restrictions.or";
		public static final String propertyEq = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyEq";
		public static final String propertyGE = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyGE";
		public static final String propertyGT = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyGT";
		public static final String propertyIn = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyIn";
		public static final String propertyIsNull = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyIsNull";
		public static final String propertyLE = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyLE";
		public static final String propertyLT = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyLT";
		public static final String propertyLike = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyLike";
		public static final String propertyNE = "meteor:com.googlecode.meteorframework.core.query.Restrictions.propertyNE";
		public static final String typeEq = "meteor:com.googlecode.meteorframework.core.query.Restrictions.typeEq";
		public static final String uriEq = "meteor:com.googlecode.meteorframework.core.query.Restrictions.uriEq";

	}

	public static interface Selector {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Selector";

		// properties
		public static final String range = "meteor:com.googlecode.meteorframework.core.query.Selector.range";
		public static final String restriction = "meteor:com.googlecode.meteorframework.core.query.Selector.restriction";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Selector$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.query.Selector$Constructor.create";

		}

	}

	public static interface CompoundRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.CompoundRestriction";

		// properties
		public static final String operator = "meteor:com.googlecode.meteorframework.core.query.CompoundRestriction.operator";
		public static final String restrictions = "meteor:com.googlecode.meteorframework.core.query.CompoundRestriction.restrictions";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.CompoundRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.query.CompoundRestriction$Constructor.create";

		}

	}

	public static interface URIRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.URIRestriction";

		// properties
		public static final String operator = "meteor:com.googlecode.meteorframework.core.query.URIRestriction.operator";
		public static final String value = "meteor:com.googlecode.meteorframework.core.query.URIRestriction.value";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.URIRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.query.URIRestriction$Constructor.create";

		}

	}

	public static interface Operator {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Operator";

		// properties
		public static final String text = "meteor:com.googlecode.meteorframework.core.query.Operator.text";

		// methods
		public static final String toString = "meteor:com.googlecode.meteorframework.core.query.Operator.toString";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Operator$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.query.Operator$Constructor.create";

		}

	}

	public static interface SimpleRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.SimpleRestriction";

		// properties
		public static final String operator = "meteor:com.googlecode.meteorframework.core.query.SimpleRestriction.operator";
		public static final String property = "meteor:com.googlecode.meteorframework.core.query.SimpleRestriction.property";
		public static final String value = "meteor:com.googlecode.meteorframework.core.query.SimpleRestriction.value";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.SimpleRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.core.query.SimpleRestriction$Constructor.create";

		}

	}

	public static interface Restriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.core.query.Restriction";

	}



	public void contributeMetaData(com.googlecode.meteorframework.core.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
