package com.googlecode.meteorframework.utils;

import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * Wrapper for a plugin class loader. This class is only needed because the
 * current PluginClassLoader is not clearly exposed as a URLClassLoader and its
 * getURLs() method does not properly return the list of url's (it misses
 * required jars, etc.)
 */
public class PluginClassLoaderWrapper extends BundleClassloader {
	public PluginClassLoaderWrapper(String plugin) 
	{
		try {
			Bundle bundle= Platform.getBundle(plugin);
			addBundle(bundle);
			Set<ManifestElement> dependencies= OsgiUtils.getAllDependencies(bundle);
			for (ManifestElement idb : dependencies)  
				addBundle(Platform.getBundle(idb.getValue()));
		}
		catch (BundleException x) {
			throw new RuntimeException("Error create classloader for plugin "+plugin, x);
		}
	}
}
