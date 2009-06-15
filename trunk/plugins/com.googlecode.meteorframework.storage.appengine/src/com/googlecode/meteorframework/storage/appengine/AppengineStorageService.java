package com.googlecode.meteorframework.storage.appengine;

import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.storage.StorageService;

/**
 * The URL to connect to an appengine storage server must begin 
 * with meteor:storage:appengine and should specify the root 
 * entity group to use.
 * For instance...
 *  meteor:storage:appengine/customer19477
 * ...connects to google storage and uses the root entity 
 * with the name 'customer19477' as the parent for all new 
 * entities.
 * 
 * If the specified entity does not exist then an exception will 
 * be thrown.  If the 'create' parameter is included in the 
 * URL then the root entity will be created if it is not found.
 * For instance the URL...
 *  meteor:storage:appengine/customer19477?create=true
 * ...will cause the a root entity named 'customer19477' to be 
 * created if it does not exist. 
 * 
 *  
 *  
 * @author Ted Stockwell
 */
@Model 
public interface AppengineStorageService
extends StorageService
{

}
