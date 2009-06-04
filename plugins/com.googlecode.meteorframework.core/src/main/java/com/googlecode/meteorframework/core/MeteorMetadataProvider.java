package com.googlecode.meteorframework.core;

/**
 * Implemented by classes that are used to populate a Meteor repository with 
 * metadata.
 * 
 * Instead of pulling metadata from the provider a repository is passed to the 
 * provider and the provider pushes its contributions into the repository.
 * This is because a provider may not only add metadata to a repository but 
 * may also make changes to existing metadata.
 * 
 * @author ted stockwell
 */
public interface MeteorMetadataProvider {
	
	void contributeMetaData(Scope repository);
	
}
