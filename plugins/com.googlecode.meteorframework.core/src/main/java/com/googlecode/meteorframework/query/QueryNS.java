package com.googlecode.meteorframework.query;

import com.googlecode.meteorframework.JavaMeteorMetadataProvider;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Generated;

@Generated public class QueryNS extends JavaMeteorMetadataProvider {

	public static final String NAMESPACE= "meteor:com.googlecode.meteorframework.query";

	public static final String BUNDLE= "com.googlecode.meteorframework.core";

	public static interface Selector {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Selector";

		// properties
		public static final String range = "meteor:com.googlecode.meteorframework.query.Selector.range";
		public static final String restriction = "meteor:com.googlecode.meteorframework.query.Selector.restriction";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Selector$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.query.Selector$Constructor.create";

		}

	}

	public static interface Restrictions {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Restrictions";

		// methods
		public static final String and = "meteor:com.googlecode.meteorframework.query.Restrictions.and";
		public static final String not = "meteor:com.googlecode.meteorframework.query.Restrictions.not";
		public static final String or = "meteor:com.googlecode.meteorframework.query.Restrictions.or";
		public static final String propertyEq = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyEq";
		public static final String propertyGE = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyGE";
		public static final String propertyGT = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyGT";
		public static final String propertyIn = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyIn";
		public static final String propertyIsNull = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyIsNull";
		public static final String propertyLE = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyLE";
		public static final String propertyLT = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyLT";
		public static final String propertyLike = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyLike";
		public static final String propertyNE = "meteor:com.googlecode.meteorframework.query.Restrictions.propertyNE";
		public static final String typeEq = "meteor:com.googlecode.meteorframework.query.Restrictions.typeEq";
		public static final String uriEq = "meteor:com.googlecode.meteorframework.query.Restrictions.uriEq";

	}

	public static interface Operators {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Operators";

		// properties
		public static final String ull = "meteor:com.googlecode.meteorframework.query.Operators.ull";

		// methods
		public static final String and = "meteor:com.googlecode.meteorframework.query.Operators.and";
		public static final String eq = "meteor:com.googlecode.meteorframework.query.Operators.eq";
		public static final String ge = "meteor:com.googlecode.meteorframework.query.Operators.ge";
		public static final String gt = "meteor:com.googlecode.meteorframework.query.Operators.gt";
		public static final String le = "meteor:com.googlecode.meteorframework.query.Operators.le";
		public static final String like = "meteor:com.googlecode.meteorframework.query.Operators.like";
		public static final String lt = "meteor:com.googlecode.meteorframework.query.Operators.lt";
		public static final String ne = "meteor:com.googlecode.meteorframework.query.Operators.ne";
		public static final String not = "meteor:com.googlecode.meteorframework.query.Operators.not";
		public static final String or = "meteor:com.googlecode.meteorframework.query.Operators.or";

	}

	public static interface SimpleRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.SimpleRestriction";

		// properties
		public static final String operator = "meteor:com.googlecode.meteorframework.query.SimpleRestriction.operator";
		public static final String property = "meteor:com.googlecode.meteorframework.query.SimpleRestriction.property";
		public static final String value = "meteor:com.googlecode.meteorframework.query.SimpleRestriction.value";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.query.SimpleRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.query.SimpleRestriction$Constructor.create";

		}

	}

	public static interface Restriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Restriction";

	}

	public static interface Operator {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Operator";

		// properties
		public static final String text = "meteor:com.googlecode.meteorframework.query.Operator.text";

		// methods
		public static final String toString = "meteor:com.googlecode.meteorframework.query.Operator.toString";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.query.Operator$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.query.Operator$Constructor.create";

		}

	}

	public static interface InRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.InRestriction";

		// properties
		public static final String property = "meteor:com.googlecode.meteorframework.query.InRestriction.property";
		public static final String values = "meteor:com.googlecode.meteorframework.query.InRestriction.values";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.query.InRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.query.InRestriction$Constructor.create";

		}

	}

	public static interface URIRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.URIRestriction";

		// properties
		public static final String operator = "meteor:com.googlecode.meteorframework.query.URIRestriction.operator";
		public static final String value = "meteor:com.googlecode.meteorframework.query.URIRestriction.value";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.query.URIRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.query.URIRestriction$Constructor.create";

		}

	}

	public static interface CompoundRestriction {
		public static final String TYPE = "meteor:com.googlecode.meteorframework.query.CompoundRestriction";

		// properties
		public static final String operator = "meteor:com.googlecode.meteorframework.query.CompoundRestriction.operator";
		public static final String restrictions = "meteor:com.googlecode.meteorframework.query.CompoundRestriction.restrictions";

		public static interface Constructor {
			public static final String TYPE = "meteor:com.googlecode.meteorframework.query.CompoundRestriction$Constructor";

			// methods
			public static final String create = "meteor:com.googlecode.meteorframework.query.CompoundRestriction$Constructor.create";

		}

	}



	public void contributeMetaData(com.googlecode.meteorframework.Scope scope) {
		super.contributeMetaData(scope, BUNDLE);
	}

}
