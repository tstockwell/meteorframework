package com.googlecode.meteorframework.core.impl;

import java.net.URL;
import java.util.logging.Level;

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.SynchronousBundleListener;

import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.utils.BundleClassloader;
import com.googlecode.meteorframework.utils.FileLocator;
import com.googlecode.meteorframework.utils.Logging;
import com.googlecode.meteorframework.utils.OsgiUtils;

public class Activator implements BundleActivator {
	
	static Activator __activator;
	static Bundle __bundle;
	static BundleClassloader __bundleClassloader= new BundleClassloader();

	public static Bundle getBundle() {
		return __bundle;
	}
	
	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		return __bundleClassloader.loadClass(name);
		//return Activator.class.getClassLoader().loadClass(name);
		//return __bundle.loadClass(name);
	}
	
	public void start(BundleContext context) throws Exception {
		Scope scope= null;
		try {
			__activator= this;
			__bundle= context.getBundle();
			__bundleClassloader.addBundle(__bundle);
			
			// Cause Model system repository to get created/initialized  
			scope= SystemScopeBootstrap.getSystemScope();
		}
		catch (Throwable e) {
			e.printStackTrace();
			if (e instanceof RuntimeException) 
				throw (RuntimeException)e;
			if (e instanceof Error) 
				throw (Error)e;
			throw (Exception)e;
		}
		final Scope systemScope= scope;
		
		// add Meteor bundle before all others
		Bundle meteorBundle= context.getBundle();
		__bundleClassloader.addBundle(meteorBundle);
		OsgiMetadataDiscoverer.loadMetadata(meteorBundle, systemScope);
		SystemScopeBootstrap.enableSystemBindings();
		
		
		// add metadata in other plugins that are already activated  
		Bundle[] bundles= context.getBundles();
		bundles= OsgiUtils.sortBundlesByDependencies(bundles);
		for (Bundle bundle : bundles) {
			
			if (__bundleClassloader.containsBundle(bundle))
				continue;

			boolean loadMetadata= true;
			if (bundle.getBundleId() == __bundle.getBundleId())
				loadMetadata= false;
			if (bundleUsesMeteor(bundle) == false)
				loadMetadata= false;

			if (loadMetadata) 
			{
				__bundleClassloader.addBundle(bundle);
				OsgiMetadataDiscoverer.loadMetadata(bundle, systemScope);
			}
			
			URL n3URL= bundle.getEntry("/meteor.n3");
			if (n3URL != null) {
				OsgiMetadataDiscoverer.loadMetadataFile(FileLocator.resolve(n3URL), systemScope);
				__bundleClassloader.addBundle(bundle);
			}			
		}
		
		// add metadata in other plugins that are activated in the future 
		context.addBundleListener(new SynchronousBundleListener() {

			public void bundleChanged(BundleEvent event) {
				try {
					int eventType= event.getType();
					if (BundleEvent.STARTED != eventType)
						return;
					
					Bundle bundle= event.getBundle();
					if (bundle.getBundleId() == __bundle.getBundleId())
						return;
					if (bundleUsesMeteor(bundle) == false)
						return;
					if (__bundleClassloader.containsBundle(bundle))
						return;

					__bundleClassloader.addBundle(bundle);
					OsgiMetadataDiscoverer.loadMetadata(bundle, systemScope);
					
					
					URL n3URL= bundle.getEntry("/meteor.n3");
					if (n3URL != null) {
						OsgiMetadataDiscoverer.loadMetadataFile(n3URL, systemScope);
						__bundleClassloader.addBundle(bundle);
					}
					
				} 
				catch (BundleException e) {
					Logging.getLogger().log(Level.WARNING, "Error while loading metadata", e);
				}
			}
		});
		
	}



	static protected boolean bundleUsesMeteor(Bundle bundle) throws BundleException {
		String header= (String)bundle.getHeaders().get(Constants.REQUIRE_BUNDLE);
		if (header == null)
			return false;
		ManifestElement[] requiredBundles = 
			ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, header);
		if (requiredBundles == null) 
			return false;
		boolean usesMeteor= false;
		for (int i= 0; !usesMeteor && i < requiredBundles.length; i++) {
			try {
				ManifestElement element= requiredBundles[i]; 
				String requiredBundle = element.getValue();
				if (requiredBundle.equals(__bundle.getSymbolicName())) 
					usesMeteor= true;
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return usesMeteor;
	}

	public void stop(BundleContext context) throws Exception {
	}

	public static BundleClassloader getMeteorClassloader() {
		return __bundleClassloader;
	}
	
}
