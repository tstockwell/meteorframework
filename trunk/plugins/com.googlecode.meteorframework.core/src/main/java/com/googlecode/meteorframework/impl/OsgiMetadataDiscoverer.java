package com.googlecode.meteorframework.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;

import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.MeteorMetadataProvider;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.core.utils.TurtleReader;
import com.googlecode.meteorframework.utils.FileLocator;

@SuppressWarnings("unchecked")
public class OsgiMetadataDiscoverer {
	
	public static void loadMetadata(Bundle bundle, Scope scope) {
		
		URL url= bundle.getEntry("/META-INF/services/com.googlecode.meteorframework.MeteorMetadataProvider");
		if (url == null)
			return;
		
		Properties properties= new Properties();
		InputStream inStream= null;
		try { 
			inStream= url.openStream();
			properties.load(inStream); 
		}
		catch (Throwable t) {
			throw new MeteorException("Could not load service manifest:"+url, t);
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
				Logger logger= Logger.getLogger(OsgiMetadataDiscoverer.class.getName());
				logger.log(Level.WARNING, "Could not load service class:"+serviceName, t);
				continue;
			}
			
			Object object= null;
			try {
				object= serviceClass.newInstance();
			}
			catch (Throwable t) {
				Logger logger= Logger.getLogger(OsgiMetadataDiscoverer.class.getName());
				logger.log(Level.SEVERE, "Could not instantiate service:"+serviceName, t);
				continue;
			}

			if ((object instanceof MeteorMetadataProvider) == false) {
				Logger logger= Logger.getLogger(OsgiMetadataDiscoverer.class.getName());
				logger.log(Level.SEVERE, "Service does not implement "+MeteorMetadataProvider.class.getName()+":"+serviceName);
				continue;
			}
			
			MeteorMetadataProvider provider= (MeteorMetadataProvider)object;
			try {
				provider.contributeMetaData(scope);
			}
			catch (Throwable t) {
				Logger logger= Logger.getLogger(OsgiMetadataDiscoverer.class.getName());
				logger.log(Level.SEVERE, "Error occurred while contributing metadata.", t);
				continue;
			}
		}

	}
//	public static void loadMetadata(Bundle bundle, DefaultRepositoryImpl repository) {
//		
//		try {
//			Collection<URL> urls= getBundleClasspathURLs(bundle);
//			
//			List<Class<?>> classes= new ArrayList<Class<?>>();
//			for (URL url : urls) {
//				File file= new File(url.getFile());
//				List<String> names= findClasses(file);
//				List<Class<?>> allClasses= loadClasses(bundle, names);
//				for (Class class1 : allClasses) {
//					if (class1.isAnnotationPresent(Model.class))
//						classes.add(class1);
//				}
//			}
//			
//			registerAnnotationHandlers(classes);
//			
//			// load behavior
//			List<ModelAnnotationHandler> metadata= findOverloads(classes, repository);
//			for (ModelAnnotationHandler handler : metadata) {
//				handler.addBehavior(repository);
//			}
//			
//			// load Java-based type definitions
//			findJavaTypes(classes, repository);
//			
//			// load modules
//			findModules(classes, repository);
//			
//			for (ModelAnnotationHandler handler : metadata) {
//				handler.addDerivedMetadata(repository);
//			}
//			
//		}
//		catch (Throwable e) {
//			e.printStackTrace();
//			throw new MeteorException("Error loading metadata for bundle:"+bundle, e);
//		}
//
//	}

	/**
	 * Loads the bundle's meteor.n3 file, if any 
	 */
	public static void loadMetadataFile(URL url, Scope scope) {
		try {
			TurtleReader reader= new TurtleReader(url);
			reader.addMetadataToScope(scope);
		} catch (Throwable e) {
			throw new MeteorException("Failed to read metadata file:"+FileLocator.resolve(url), e);
		}
	}

//	public static Collection<Class> getDependencies(Class c1) {
//		ArrayList<Class> depends= new ArrayList<Class>();
//		Field[] fields= c1.getDeclaredFields();
//		for (Field field : fields) {
//			if (Module.class.isAssignableFrom(field.getType())) {
//				depends.add(field.getType());
//			}
//		}
//		return depends;
//	}

}
