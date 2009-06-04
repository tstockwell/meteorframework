package com.googlecode.meteorframework.core;

import com.googlecode.meteorframework.core.annotation.Model;

/**
 * A binding represents some client-visible semantic of an API 
 * implementation that is satisfied by some implementations of the API and not by 
 * others. 
 * For example, we could introduce bindings representing synchronicity 
 * and asynchronicity.  Or bindings representing production code and test code.
 * Or bindings that represent different customers.
 * 
 * For instance, here is how bindings might be used to 
 * create synchronous and asynchronous implementations of a base type...
 * 
 * <code>
 * @Binding(Synchronous.class)
 * class SynchronousPaymentProcessor implements PaymentProcessor {
 * 		 ...
 * }
 * </code>
 * <code>
 * @Binding(Asynchronous.class)
 * class AsynchronousPaymentProcessor implements PaymentProcessor {
 * 		...
 * }
 * </code>
 * 
 * Finally, bindings are applied to injection points to distinguish which 
 * implementation is required by the client. 
 * For example, when Meteor encounters the following injected field, an instance 
 * of SynchronousPaymentProcessor will be injected:
 * <code>
 *		@Inject @Binding(Synchronous.class) PaymentProcessor paymentProcessor;
 * </code>
 * 
 * But in this case, an instance of AsynchronousPaymentProcessor will be injected:
 * <code>
 * 		@Inject @Binding(Asynchronous.class) PaymentProcessor paymentProcessor;
 * </code>
 *  
 * 
 * @author Ted Stockwell
 */
@Model public abstract class BindingType
{
}
