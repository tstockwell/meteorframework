package com.googlecode.meteorframework.core;

/**
 * In many applications, there are various implementations of a particular API, 
 * and the implementation used at runtime varies between different deployments 
 * of the system. 
 * For instance, there may be different deployments for testing vs production,
 * different deployments for different locations within an enterprise, or 
 * different deployments for different clients.
 * 
 * A Deployment type represents a deployment scenario. 
 * Model elements may be classified by deployment type, and thereby associated 
 * with various deployment scenarios.
 * Deployment types allow Meteor to identify which model elements should be enabled 
 * for use in a particular deployment of the system.
 * 
 * The concept of Deployment in Meteor is meant to be equivalent to the concept 
 * of deployment type in Web Beans.  By modeling the concept of deployment 
 * Meteor makes it possible for plugins to expand or customize deployments. 
 * 
 * @author Ted Stockwell
 *
 */
public interface Deployment extends Resource
{

}
