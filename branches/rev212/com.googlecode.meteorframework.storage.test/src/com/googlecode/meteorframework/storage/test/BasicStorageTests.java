package com.googlecode.meteorframework.storage.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.annotation.Inject;
import com.googlecode.meteorframework.core.query.Restrictions;
import com.googlecode.meteorframework.core.query.Selector;
import com.googlecode.meteorframework.storage.StorageService;
import com.googlecode.meteorframework.storage.StorageSession;
import com.googlecode.meteorframework.storage.StorageTask;
import com.googlecode.meteorframework.test.BaseMeteorTest;

 
public class BasicStorageTests extends BaseMeteorTest
{
	static final String DESCRIPTION= "Test Object";
	
	private @Inject Restrictions _restrictions;
	private @Inject Scope _scope;
	private @Inject StorageService _storageService;
	private @Inject Selector.Constructor _selectorFactory;
	
	public void testBasicPersistence()
	throws Exception
	{
		StorageSession session= _storageService.openSession();
		 
		Resource resource1= _scope.createInstance(Resource.class);
		resource1.setDescription(DESCRIPTION);
		session.persist(resource1);
		
		session.close();		
		session= _storageService.openSession();
		
		assertNotNull(resource1.getURI());
		 
		// try reading the resource just created		
		Resource resource2= session.findByURI(Resource.class, resource1.getURI());
		
		assertNotNull(resource2);
		assertEquals(DESCRIPTION, resource1.getDescription());
		assertEquals(DESCRIPTION, resource2.getDescription());

		session.close();
	}
	
	public void testCompositePersistence()
	throws Exception
	{
		StorageSession session= _storageService.openSession();
		 
		Order order= _scope.getInstance(Order.class);
		Set<Item> items= order.getItems();
		for (int i= 10; 0 < i--;)
			items.add(_scope.getInstance(Item.class));
		session.persist(order);
		session.flush();
		
		// try reading the resource just created		
		Order order2= session.findByURI(Order.class, order.getURI());
		
		assertNotNull(order2);
		Set<Item> items2= order2.getItems();
		assertEquals(items.size(), items2.size());
		
		for (Item item : items)
			assertTrue(items2.contains(item));

		session.close();
	}
	
	public void testCompositeDelete()
	throws Exception
	{
		StorageSession session= _storageService.openSession();
		 
		Order order= _scope.getInstance(Order.class);
		Set<Item> items= order.getItems();
		for (int i= 10; 0 < i--;)
			items.add(_scope.getInstance(Item.class));
		session.persist(order);
		session.flush();
		
		Order order2= session.findByURI(Order.class, order.getURI());
		
		Set<Item> items2= order2.getItems();
		assertEquals(items.size(), items2.size());
		
		session.delete(order2.getURI());
		for (Item item : items2) {
			try {
				Item item2= session.findByURI(Item.class, item.getURI());
				fail("We found an object in the storage system that should have been deleted:"+item2);
			}
			catch (MeteorNotFoundException x) {
			}
		}

		session.close();
	}
	
	public void testOrderedCollectionPersistence()
	throws Exception
	{
		StorageSession session= _scope.getInstance(StorageSession.class);
		 
		Order order= _scope.getInstance(Order.class);
		List<Item> items= order.getOrderedItems();
		for (int i= 10; 0 < i--;)
			items.add(_scope.getInstance(Item.class));
		session.persist(order);
		session.flush();
		
		// try reading the resource just created		
		Order order2= session.findByURI(Order.class, order.getURI());
		
		assertNotNull(order2);
		List<Item> items2= order2.getOrderedItems();
		assertEquals(items.size(), items2.size());
		
		for (int i= 0; i < items.size(); i++)
			assertEquals(items.get(i), items2.get(i));
		
		session.close();
	}
	
	public void testIndexedCollectionPersistence()
	throws Exception
	{
		StorageSession session= _storageService.openSession();
		 
		Order order= _scope.getInstance(Order.class);
		String keySource= "pnn8xdq2439085nuf14390";
		Map<String, Item> items= order.getIndexedItems();
		for (int i= 10; 0 < i--;)
			items.put(keySource.substring(0, i+1), _scope.getInstance(Item.class));
		session.persist(order);
		session.flush();
		
		// try reading the resource just created		
		Order order2= session.findByURI(Order.class, order.getURI());
		
		assertNotNull(order2);
		Map<String, Item> items2= order2.getIndexedItems();
		assertEquals(items.size(), items2.size());
		
		for (String key : items.keySet()) {
			Item item1= items.get(key);
			Item item2= items2.get(key);
			assertEquals(item1, item2);
		}
		
		session.close();
	}
	
	public void testBasicCriteriaQueries()
	throws Exception
	{
		StorageSession session= _storageService.openSession();
		 
		Order order= _scope.getInstance(Order.class);
		String keySource= "pnn8xdq2439085nuf14390";
		Map<String, Item> items= order.getIndexedItems();
		for (int i= 10; 0 < i--;)
			items.put(keySource.substring(0, i+1), _scope.getInstance(Item.class));
		session.persist(order);
		session.flush();
		
		// try reading the resource just created
		Selector<Order> criteria= _selectorFactory.create(Order.class, _restrictions.uriEq(order));
		List<Order> orders= session.list(criteria);
		assertEquals(1, orders.size());
		assertTrue(orders.contains(order));
		assertEquals(10, orders.get(0).getIndexedItems().size());
		
		session.close();
	}
	
	public void testTaskSession()
	throws Exception
	{
		String result= _storageService.run(new StorageTask<String>() {
			@Override
			public String run(StorageSession session)
			{
				assertNotNull(session);
				return "done";
			}
		});
		
		assertEquals("done", result);
	}

}
