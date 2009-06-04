package com.googlecode.meteorframework.test;

import java.util.Collection;
import java.util.Set;

import com.googlecode.meteorframework.core.BindingContext;
import com.googlecode.meteorframework.core.BindingType;
import com.googlecode.meteorframework.core.Meteor;
import com.googlecode.meteorframework.core.MeteorException;
import com.googlecode.meteorframework.core.MeteorNS;
import com.googlecode.meteorframework.core.Property;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.Type;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.binding.Testing;
import com.googlecode.meteorframework.core.query.URIRestriction;
import com.googlecode.meteorframework.core.test.extension.ExtensionNS;
import com.googlecode.meteorframework.test.extension.CustomerExtension;
import com.googlecode.meteorframework.test.extension.CustomerStatementAdvice;



/**
 * Basic tests to get Meteor up and running.
 * Doesn't include tests for dependency injection 
 *  
 * @author Ted Stockwell
 */
@SuppressWarnings("unchecked")
public class BootstrapTests 
extends BaseMeteorTest
{
	static final String DESCRIPTION= "klja;lkjdfasdfasdf8888";
	public static final String TEST_VALUE = "a;ldjfa;lfjd;alkfjd";
	
	@Inject private Scope _scope;
	
	@Inject private URIRestriction.Constructor uriRestrictions;
	@Inject private Customer.Constructor customers;
	@Inject private CustomerStatement.Constructor statements;
	@Inject private CustomerSubclass.Constructor customerSubclasses;
	
	@Inject private Customer _customer;
	
	public void testResourceEquality() 
	throws Throwable  
	{
		Resource resource= _scope.createInstance(Resource.class);
		
		Type resourceType= _scope.findResourceByURI(MeteorNS.Resource.TYPE, Type.class);
		assertSame(resourceType, resource.getType());
		assertEquals(resourceType, resource.getType());
		assertEquals(MeteorNS.Resource.TYPE, resourceType.getURI());
		
		Property<?> descriptionProperty= _scope.findResourceByURI(MeteorNS.Resource.description, Property.class);
		assertNotNull("Property not found :"+MeteorNS.Resource.description, descriptionProperty);
		assertEquals("Not correct Type", MeteorNS.Property.TYPE, descriptionProperty.getType().getURI());
		
		Resource resource2= _scope.createInstance(Resource.class);
		resource2.setURI(resource.getURI());
		assertTrue(resource.equals(resource2));
		assertTrue(resource.hashCode() == resource2.hashCode());
		
	}
	
	public void testClassVariableInitialization() 
	throws Throwable  
	{
		Property<?> decsriptionProperty= _scope.findResourceByURI(MeteorNS.Resource.description, Property.class);
		assertNotNull("Property not found :"+MeteorNS.Resource.description, decsriptionProperty);
		assertEquals("Not correct Type", MeteorNS.Property.TYPE, decsriptionProperty.getType().getURI());
		
		// create a URIRestriction in order to test that service classes can 
		//create and initialize class variables
		URIRestriction uriCriterion= uriRestrictions.create(decsriptionProperty.getURI());
		assertNotNull(uriCriterion);
		assertEquals("Constructor failed to properly initialize a class variable", decsriptionProperty.getURI(), uriCriterion.getValue());
	}
	
	public void testModelAnnotationProperties() 
	throws Throwable  
	{
		Type type= _scope.findResourceByURI(TestNS.AnnotationTestType.TYPE, Type.class);
		assertNotNull(type);
		String testValue= type.getProperty(MeteorNS.Resource.description);
		assertEquals("Properties set in annotations seem to not be working", TEST_VALUE, testValue);
	}
	
	public void testPostConstructAnnotation() 
	throws Throwable  
	{
		AnnotationTestType testType= _scope.getInstance(AnnotationTestType.class);
		assertNotNull("The @PostConstruct annotation is probably not working", testType.getResource());
	}
	
	public void testSemanticEquivalentAnnotations() 
	throws Throwable  
	{
		Property<Type> extensionOfProperty= _scope.findResourceByURI(MeteorNS.Type.extensionOf, Property.class);
		assertNotNull(extensionOfProperty);
		Set<Property<Type>> inverseOf= extensionOfProperty.getInverseOf();
		assertNotNull("Multivalued properties should return empty collections, not null", inverseOf);
		assertTrue("SemanticEquivalent annotations seem to not be working", !inverseOf.isEmpty());
	}
	
	public void testDefaultValues() 
	throws Throwable  
	{
		Customer customer= customers.create("whatever");
		assertEquals("Default property values seem to not be working", DESCRIPTION, customer.getPropertyWithADefaultValue());
		
		// default properties should stay null if explicitly set to null
		customer.setPropertyWithADefaultValue(null);
		assertNull("Default properties should stay null if explicitly set to null", customer.getPropertyWithADefaultValue());
		
		Resource resource= _scope.createInstance(Resource.class);
		Customer customer2= resource.castTo(Customer.class);
		assertEquals("Default property values should also be applied when an object is cast to another type", DESCRIPTION, customer2.getPropertyWithADefaultValue());
		
		customer2.setPropertyWithADefaultValue(null);
		assertNull("Default properties should stay null if explicitly set to null", customer2.getPropertyWithADefaultValue());
		
		customer2= resource.castTo(Customer.class);
		assertNull("Default property values should NOT be reset each time an object is cast to another type, just the first time", customer2.getPropertyWithADefaultValue());
		
	}
	
	public void testSingletons() {
		SomeSingleton s1= _scope.getInstance(SomeSingleton.class);
		SomeSingleton s2= _scope.getInstance(SomeSingleton.class);
		assertSame(s1, s2);
		assertEquals(s1, s2);
		assertEquals(s1.getURI(), s2.getURI());
		
		// also test some class that is not an interface
		Object o1= _scope.getInstance(SomeBindingType.class);
		Object o2= _scope.getInstance(SomeBindingType.class);
		assertSame(o1, o2);
		assertEquals(o1, o2);
	}
	
	public void testPropertyDecorations() 
	throws Throwable  
	{
		Type integerType= _scope.findType(Integer.class);
		assertTrue("Property decorations probably not working", integerType.getScalar());
		assertTrue("is<Property> method not properly delegated to get method", integerType.isScalar());
		
	}
	
	
	public void testMixinInterceptors() 
	throws Throwable  
	{
//		TestFactory factory= repository.create(TestFactory.class);
		
		
		// Create a CustomerStatement and check the default title;
		Customer customer= customers.create("Testing Customer");
		assertFalse("The name of a customer we just just created equals CustomerSubclass.NAME. " +
				"This indicates a problem with subclass method dispatch.", CustomerSubclass.NAME.equals(customer.getName()));
		assertEquals("Geez, we just created this customer and the name's wrong already...wtf", "Testing Customer", customer.getName());
		CustomerStatement statement= statements.create(customer);
		String title= statement.getTitle();
		assertEquals(CustomerStatementAdvice.getCustomTitle(customer), title);
		
		// make sure the customer type has the mixed-in properties from the 
		// ExtendedCustomer class.
		Type customerType= _scope.findType(Customer.class);		
		assertNotNull("Customer Type not found.", customerType);		
		Type customerXType= _scope.findType(CustomerExtension.class);		
		assertNotNull("CustomerExtension Type not found.", customerXType);		
		Collection<Type> customerExtensions= customerType.getExtensions();
		assertTrue("Customer Type is missing extension", customerExtensions.contains(customerXType));
		
		// test extension
		CustomerExtension xcustomer= customer.castTo(CustomerExtension.class);
		assertNotNull(xcustomer);
		assertEquals("An extension Role should have the same URI as its base object", customer.getURI(), xcustomer.getURI());
		assertEquals(customer.getName(), xcustomer.getName());
		xcustomer.setTaxId("Any Ole Tax ID");
		assertEquals(xcustomer.getTaxId(), "Any Ole Tax ID");
		assertEquals(xcustomer.getTaxId(), customer.getProperty(ExtensionNS.CustomerExtension.taxId));
		
	}
	
	public void testSubclassMethodDispatch() {
		CustomerSubclass customer= customerSubclasses.create("Testing Customer");		
		assertEquals("Subclass method dispatch not working", CustomerSubclass.NAME, customer.getName());
		
//		// test dispatch for role subclasses 
//		Resource resource= repository.getService(Resource.Constructor.class).create(Resource.class);
//		Customer customerRole= resource.castTo(Customer.class);
//		customerRole.setName("Testing Name");
//		assertEquals("Subclass method dispatch not working, probably called a decorator that was applied to a subclass", "Testing Name", customerRole.getName());		
//		CustomerSubclass customerSubclassRole= resource.castTo(CustomerSubclass.class);
//		assertEquals("Subclass method dispatch not working", CustomerSubclass.NAME, customerSubclassRole.getName());
	}
	
	public void testMeteorManifest() {
		Resource resource= _scope.findResourceByURI("meteor:com.googlecode.meteorframework.test.CustomerFromManifest");
		assertNotNull("Did not find resource defined in Meteor manifest", resource);
		Type type= resource.getType();
		assertEquals(_scope.findType(Customer.class), type);
	}
	
	/**
	 * Creates a Customer and then adds a decorator to it on the fly.
	 */
	public void testDynamicDecorator() {
		Customer customer= customers.create("Testing Name");
		CustomerDecorator decorator= customer.addDecorator(CustomerDecorator.class);
		assertNotNull(decorator);
		assertEquals(CustomerDecorator.NAME, customer.someMethod());
		assertEquals(CustomerDecorator.NAME, customer.getName());
		
		customer= customers.create("Testing Name");
		assertEquals("", customer.someMethod());
		assertEquals("Testing Name", customer.getName());		
	}
	
	public void testMultiMethodBinding() {
		/*
		 * Get system configuration service.
		 * Should return 'system' values.
		 */
		Scope systemContext= Meteor.getSystemScope();
		SomeConfiguration configuration= systemContext.getInstance(SomeConfiguration.class);
		assertEquals(SomeConfiguration.BASE_VALUE, configuration.doSomething());
		assertEquals(SomeConfiguration.BASE_VALUE, configuration.getSomeProperty());
		
		/*
		 * Get a 'test' configuration service from the base scope.
		 * Should return 'test' values.
		 */
		Testing testFacet= systemContext.getInstance(Testing.class);
		SomeConfiguration testConfiguration= systemContext.getInstance(SomeConfiguration.class, testFacet);
		assertEquals(SomeConfiguration.TEST_VALUE, testConfiguration.getSomeProperty());
		assertEquals(SomeConfiguration.TEST_VALUE, testConfiguration.doSomething());
		
		/*
		 * Get a 'test' configuration service.
		 * Should return 'test' values.
		 */
		Scope testContext= systemContext.createScope(testFacet);
		testConfiguration= testContext.getInstance(SomeConfiguration.class);
		assertEquals(SomeConfiguration.TEST_VALUE, testConfiguration.getSomeProperty());
		assertEquals(SomeConfiguration.TEST_VALUE, testConfiguration.doSomething());
		
	}
	
	public void testPostConstruct() {
		Customer customer= _scope.getInstance(Customer.class);
		assertTrue("PostConstruct method not invoked when a resource is created by Scope.getInstance", CustomerImpl.postConstructWasCalled(customer));
		
		assertTrue("PostConstruct method not invoked when a resource is @Injected", CustomerImpl.postConstructWasCalled(_customer));
		
		customer= customers.create("test");
		assertTrue("PostConstruct method not invoked when a resource is created by Customer.Constructor", CustomerImpl.postConstructWasCalled(customer));
		
		customer= _scope.getInstance(CustomerSubclass.class);
		assertTrue("PostConstruct method not invoked for base class when a resource is created by Scope.getInstance", CustomerImpl.postConstructWasCalled(customer));
	}
	
	/**
	 * This test makes sure that Decorators can decorate a property with a set method 
	 * even if the associated @Model interface does not declare a set method.  
	 */
	public void testGetOnlyPropertySetDecoration() {
		Customer customer= _scope.getInstance(Customer.class);
		customer.setProperty(TestNS.Customer.propertyWithNoSetMethod, "someValue");
		String value= customer.getPropertyWithNoSetMethod();
		String msg= "Set method decorator not working when associated @Model interface does not declare a set method";
		assertEquals(msg, "someValue"+CustomerImpl.SET_INTERCEPTED_INDICATOR, value);
	}
	
	public void testWriteOnceProperties() {
		Customer customer= _scope.getInstance(Customer.class);
		customer.setWriteOnceProperty("value");
		assertEquals("value", customer.getWriteOnceProperty());
		try {
			customer.setWriteOnceProperty("value2");
			throw new RuntimeException("Write-once property failed to throw exception on 2nd write");
		}
		catch (MeteorException t) {
		}
		assertEquals("value", customer.getWriteOnceProperty());
	}
	
	public void testScopeBindingProblem() {
		// create new scope
		Scope newScope= _scope.createScope(_scope.getInstance(SomeBindingType.class));
		
		/*
		 * now make sure we can still we can still create an object with the new scope.
		 * At one time there was a bug that caused the following call to fail.
		 */
		SomeScopedType scopedType= newScope.getInstance(SomeScopedType.class);
		assertNotNull(scopedType);
		
		assertEquals(newScope, ((Resource)scopedType).getScope());
	}
	
	public void testScopeInstanceBindings() {
		
		CustomerSubclass customer= _scope.getInstance(CustomerSubclass.class);
		_scope.putInstance(customer, CustomerSubclass.class);
		CustomerSubclass customer2= _scope.findInstance(CustomerSubclass.class);
		assertSame("Scope.findInstance does not seem to be working", customer, customer2);
		
		Scope newScope= _scope.createScope(_scope.getInstance(SomeBindingType.class));
		customer2= newScope.findInstance(CustomerSubclass.class);
		assertSame("Scope.findInstance does not seem to be working for nested scopes", customer, customer2);
		
		customer2= newScope.findInstance(CustomerSubclass.class, _scope.getInstance(SomeBindingType.class));
		assertSame("Scope.findInstance does not seem to be working when bindings are used", customer, customer2);
		
		Customer customer3= newScope.findInstance(Customer.class);
		assertEquals("Scope.putInstance/findInstance does not work when super classes are fetched", customer, customer3);
	}
	
	public void testFindResourcesByType() {
		
		/*
		 * This test checks for a bug that caused a call to 
		 * findResourcesByType to toss an exception if no resources 
		 * were found.
		 */
		Set<?> found= _scope.findResourcesByType("aBogusTypeName");
		assertTrue(found.isEmpty());
		
		
		found= _scope.findResourcesByType(Type.class);
		assertFalse(found.isEmpty());
	}
	
	public void testMultivaluePropertyRange() {
		Property<BindingContext> facetsProperty= 
			_scope.findResourceByURI(MeteorNS.Resource.facets, Property.class);
	
		String msg= "The range of a multi-valued property should be the value type, not the container type";
		assertEquals(msg, BindingType.class, facetsProperty.getRange().getJavaType());
	}
}
