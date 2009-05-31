package com.googlecode.meteorframework.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

@SuppressWarnings("unchecked")
public class OsgiUtils
{
	final static private Map<Bundle, Set<String>> __dependenciesByBundle= new WeakHashMap<Bundle, Set<String>>();
	final static private Map<Bundle, List<String>> __requiredBundles= new WeakHashMap<Bundle, List<String>>();
	final static private Map<Bundle, List<String>> __exportedPackages= new WeakHashMap<Bundle, List<String>>();
	
	
	public static Bundle[] sortBundlesByDependencies(Bundle[] bundles) 
	{
		/*
		 * not any sort will work, gotta be sure that all elements will be compared.
		 * I think that maybe this is because the dependency relation between 
		 * bundles is not transitive.
		 */
		bubbleSort(bundles, new Comparator<Bundle>() {
			@Override public int compare(Bundle b1, Bundle b2) {
				return compareBundles(b1, b2);
			}
		});
		
		return bundles;
	}
	
	public static <T> void bubbleSort(T[] items, Comparator<T> comparator) 
	{
		int n= items.length;
		boolean swapped;
		do 
		{
			swapped= false;
			n--;
			for (int i= 0; i < n; i++) 
			{
				if (0 <= comparator.compare(items[i], items[i+1])) 
				{
					T t= items[i];
					items[i]= items[i+1];
					items[i+1]= t;
					swapped= true;
				}
			}
		} while (swapped);
	}
	

	/**
	 * Returns names of bundles on which the given Bundle is dependent.
	 * Is recursive, so it returns all dependencies, not just those listed by the bundle itself.
	 * @throws BundleException
	 */
	public static Set<String> getAllDependencies(Bundle bundle) throws BundleException {
		Set<String> dependencies= __dependenciesByBundle.get(bundle);
		if (dependencies == null) {
			final Map<String, Bundle> bundleRegistry= new HashMap<String, Bundle>();
			BundleContext bundleContext= bundle.getBundleContext();
			if (bundleContext != null) {
				for (Bundle b : bundleContext.getBundles())
					bundleRegistry.put(b.getSymbolicName(), b);
				dependencies= getAllDependencies(bundle, bundleRegistry);
				__dependenciesByBundle.put(bundle,dependencies);
			}
			else
				dependencies= Collections.EMPTY_SET;
		}
		

		return dependencies;
	}
	private static Set<String> getAllDependencies(Bundle bundle, Map<String, Bundle> bundleRegistry) 
	throws BundleException 
	{
		Set<String> dependencies= __dependenciesByBundle.get(bundle);
		if (dependencies == null) {
			dependencies= new HashSet<String>();
			
			List<String> requiredBundles= getRequiredBundles(bundle);
			for (String bundleName : requiredBundles) {
				Bundle bundle2= bundleRegistry.get(bundleName);
				if (bundle2 != null) {
					dependencies.add(bundleName);
					Set<String> dependencies2= getAllDependencies(bundle2, bundleRegistry);
					dependencies.addAll(dependencies2);
				}
			}
			
			__dependenciesByBundle.put(bundle,dependencies);
		}
		

		return dependencies;
	}
	
	public static final List<String> getRequiredBundles(Bundle bundle) 
	throws BundleException 
	{
		List<String> results= __requiredBundles.get(bundle);
		if (results == null) {
			
			String header= (String)bundle.getHeaders().get(Constants.REQUIRE_BUNDLE);
			if (header == null)
				return Collections.EMPTY_LIST;
			ManifestElement[] exportedPackages = 
				ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, header);
			if (exportedPackages == null) 
				return Collections.EMPTY_LIST;
			results= new ArrayList<String>();
			for (ManifestElement element : exportedPackages)
				results.add(element.getValue());
			
			__requiredBundles.put(bundle, results);
		}
		
		return results;
	}
	
	
	public static final List<String> getExportedPackages(Bundle bundle) 
	throws BundleException 
	{
		List<String> results= __exportedPackages.get(bundle);
		if (results == null) {
			String header= (String)bundle.getHeaders().get(Constants.EXPORT_PACKAGE);
			if (header == null)
				return Collections.EMPTY_LIST;
			ManifestElement[] exportedPackages = 
				ManifestElement.parseHeader(Constants.EXPORT_PACKAGE, header);
			if (exportedPackages == null) 
				return Collections.EMPTY_LIST;
			results= new ArrayList<String>();
			for (ManifestElement element : exportedPackages)
				results.add(element.getValue());
		}
		return results;
	}
	
	
	/**
	 * Returns -1 if bundle1 is dependent on bundle2.
	 * Returns 1 if bundle2 is dependent on bundle1.
	 * Returns 0 otherwise;
	 */
	public static int compareBundles(Bundle b1, Bundle b2) {
		try {
			if (getAllDependencies(b1).contains(b2.getSymbolicName()))
				return 1;
			if (getAllDependencies(b2).contains(b1.getSymbolicName()))
				return -1;
		} catch (BundleException e) {
			e.printStackTrace();
		}
		return 0;
	}
	


}
