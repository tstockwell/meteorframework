package com.googlecode.meteorframework.core;

import java.util.List;

import com.googlecode.meteorframework.core.annotation.InverseOf;
import com.googlecode.meteorframework.core.annotation.Model;



/**
 * Represents any type that can be arranged into graph structures.
 * 
 * @author Ted Stockwell
 */
@Model public interface Node
{
	/**
	 * @return any nested instances
	 */
	@InverseOf(MeteorNS.Node.parents)
	List<Object> getChildren();
	
	@InverseOf(MeteorNS.Node.children)
	List<Object> getParents();
}
