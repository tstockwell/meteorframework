package com.googlecode.meteorframework.storage.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.IsComposite;
import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement public interface Order extends Resource
{
	@IsComposite // indicates that items should be deleted when the order is deleted
	Set<Item> getItems();
	
	@IsComposite // indicates that items should be deleted when the order is deleted
	List<Item> getOrderedItems();
	
	@IsComposite // indicates that items should be deleted when the order is deleted
	Map<String, Item> getIndexedItems();
}
