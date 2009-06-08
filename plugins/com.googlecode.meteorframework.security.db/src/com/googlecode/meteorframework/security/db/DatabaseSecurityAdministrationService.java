package com.googlecode.meteorframework.security.db;

import com.googlecode.meteorframework.core.Service;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.security.SecurityAdministrationService;
import com.googlecode.meteorframework.storage.StorageService;

/**
 * An admin service that uses a Meteor StorageService to store data. 
 * 
 * @author Ted Stockwell
 */
@Model public interface DatabaseSecurityAdministrationService
extends SecurityAdministrationService
{
	@Model public interface Constructor extends Service {
		public DatabaseSecurityAdministrationService create(DatabaseSecurityService service);
	}
	
	public StorageService getStorageService();
}
