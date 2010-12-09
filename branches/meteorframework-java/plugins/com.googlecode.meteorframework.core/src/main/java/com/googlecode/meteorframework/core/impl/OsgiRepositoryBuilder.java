package com.googlecode.meteorframework.core.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.googlecode.meteorframework.core.MeteorMetadataProvider;
import com.googlecode.meteorframework.core.Scope;

/**
 * This class finds all MeteorMetadataProviders registered as plain Java 
 * services in all installed OSGi bundles and invokes the providers to populate 
 * a given repository.
 * 
 * A 'plain Java service' is a service that is listed in a manifest in the 
 * META-INF/services/ inside the bundle.
 * This is the 'standard' Java way of creating discovery information for Java service providers.
 * See http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#Service%20Provider.
 *   
 * @author ted stockwell
 */
public class OsgiRepositoryBuilder {
	
	BundleContext _bundleContext;
	Scope _repository;

	public OsgiRepositoryBuilder(BundleContext bundleContext, Scope repository) {
		_bundleContext= bundleContext;
		_repository= repository;
		
		build();
	}

	void build() {
		Bundle[] bundles= _bundleContext.getBundles();
		for (int i= 0; i < bundles.length; i++) {
			Bundle bundle= bundles[i];
			URL url= bundle.getEntry("/META-INF/services/com.googlecode.meteorframework.MeteorMetadataProvider");
			if (url == null)
				continue;
			
			Properties properties= new Properties();
			InputStream inStream= null;
			try { 
				inStream= url.openStream();
				properties.load(inStream); 
			}
			catch (Throwable t) {
				Logger logger= Logger.getLogger(getClass().getName());
				logger.log(Level.SEVERE, "Could not load service manifest:"+url);
				continue;
			}
			finally {
				try { inStream.close(); } catch (Throwable t) { }
			}
			
			for (Iterator n= properties.keySet().iterator(); n.hasNext();) {
				String serviceName= (String)n.next();
				Class serviceClass= null;
				try {
					serviceClass= bundle.loadClass(serviceName);
				}
				catch (Throwable t) {
					Logger logger= Logger.getLogger(getClass().getName());
					logger.log(Level.WARNING, "Could not load service class:"+serviceName, t);
					continue;
				}
				
				Object object= null;
				try {
					object= serviceClass.newInstance();
				}
				catch (Throwable t) {
					Logger logger= Logger.getLogger(getClass().getName());
					logger.log(Level.SEVERE, "Could not instantiate service:"+serviceName, t);
					continue;
				}

				if ((object instanceof MeteorMetadataProvider) == false) {
					Logger logger= Logger.getLogger(getClass().getName());
					logger.log(Level.SEVERE, "Service does not implement "+MeteorMetadataProvider.class.getName()+":"+serviceName);
					continue;
				}
				
				MeteorMetadataProvider provider= (MeteorMetadataProvider)object;
				try {
					provider.contributeMetaData(_repository);
				}
				catch (Throwable t) {
					Logger logger= Logger.getLogger(getClass().getName());
					logger.log(Level.SEVERE, "Error occurred while contributing metadata.", t);
					continue;
				}
			}
		}
		
	}

}
