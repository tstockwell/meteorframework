package com.googlecode.meteorframework.core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.internal.baseadaptor.DevClassPathHelper;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import com.googlecode.meteorframework.core.annotation.InjectionAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.core.annotation.Model;
import com.googlecode.meteorframework.core.annotation.ModelAnnotationHandler;
import com.googlecode.meteorframework.core.annotation.ProcessesAnnotations;
import com.googlecode.meteorframework.utils.Logging;

@SuppressWarnings("unchecked")

/**
 * A metadata provider that adds metadata defined in Java classes to a repository.  
 * 
 * The classes generated by the Meteor annotation processor from @Model 
 * annotations all extend this base class.
 */
abstract public class JavaMeteorMetadataProvider
implements MeteorMetadataProvider
{
	static final HashSet<String> __completedBundles= new HashSet<String>(); 

	public void contributeMetaData(Scope repository, String bundleName) {
		
		if (__completedBundles.contains(bundleName))
			return;
		__completedBundles.add(bundleName);
		Bundle bundle= Platform.getBundle(bundleName);
		if (bundle == null)
			throw new MeteorException("Bundle not found:"+bundleName);
		
		try {
			
			Collection<URL> urls= getBundleClasspathURLs(bundle);
			
			// register annotation handlers
			for (URL url : urls) {
				File file= new File(url.getFile());
				List<String> names= findClasses(file);
				List<Class<?>> allClasses= loadClasses(bundle, names);
				registerAnnotationHandlers(allClasses);
			}
			
			// find all classes with annotations
			final Map<String, HashSet<String>> packageDependencies= new HashMap<String, HashSet<String>>();
			List<Class<?>> classes= new ArrayList<Class<?>>();
			for (URL url : urls) {
				File file= new File(url.getFile());
				List<String> names= findClasses(file);
				List<Class<?>> allClasses= loadClasses(bundle, names);
				for (Class class1 : allClasses) {
					boolean isMeteorClass= false;
					Annotation[] annotations= class1.getDeclaredAnnotations();
					for (int i = 0; !isMeteorClass && i < annotations.length; i++) {
						Annotation annotation = annotations[i];
						if (!Meteor.getModelAnnotationHandlers(annotation.annotationType()).isEmpty()) 
							isMeteorClass= true;
					}
//					Method[] methods= class1.getDeclaredMethods();
//					for (int m = 0; !isMeteorClass && m < methods.length; m++) {
//						Method method = methods[m];
//						annotations= method.getDeclaredAnnotations();
//						for (int i = 0; !isMeteorClass && i < annotations.length; i++) {
//							Annotation annotation = annotations[i];
//							if (!Meteor.getOverloadHandlers(annotation.annotationType()).isEmpty()) 
//								isMeteorClass= true;
//						}
//					}
					
					if (class1.getName().endsWith(".package-info")) {
						java.lang.Package package1= class1.getPackage();
						Model modelAnnotation= package1.getAnnotation(Model.class);
						if (modelAnnotation != null) {
							List<String> dependencies= MeteorAnnotationUtils.getPropertyValues(modelAnnotation, CoreNS.Namespace.dependencies);
							if (!dependencies.isEmpty()) {
								HashSet<String> packageNames= new HashSet<String>();
								for (String packageURI : dependencies) {
									String packageName= packageURI;
									if (packageName.startsWith(Meteor.PROTOCOL))
										packageName= packageName.substring(Meteor.PROTOCOL.length());
									packageNames.add(packageName);
								}
								packageDependencies.put(package1.getName(), packageNames);
							}
						}
					}
					
					
					if (isMeteorClass)
						classes.add(class1);
				}
			}
			
			Collections.sort(classes, new Comparator() {
				@Override public int compare(Object o1, Object o2) {
					String p1= ((Class)o1).getPackage().getName();
					String p2= ((Class)o2).getPackage().getName();
					HashSet<String> d1= packageDependencies.get(p1);
					if (d1 != null && d1.contains(p2))
						return 1;
					HashSet<String> d2= packageDependencies.get(p2);
					if (d2 != null && d2.contains(p1))
						return -1;
					return 0;
				}
				
			});
			
			// find all annotations and create associated handlers
			List<ModelAnnotationHandler> metadata= findAnnotations(classes, repository);
			
			// load behavior
			for (ModelAnnotationHandler handler : metadata) {
				handler.addBehavior();
			}
			
			// load type definitions (and all the associated properties and methods)
			for (ModelAnnotationHandler handler : metadata) {
				handler.addTypeDefinitions();
			}
			//findJavaTypes(classes, repository);
			
			// set property values specified in @Model annotations
			for (ModelAnnotationHandler handler : metadata) {
				handler.addAnnotationMetadata();
			}
			//addAnnotationMetadata(classes, repository);
			
//			// load modules
//			findModules(classes, repository);
			
			// finally, create any derived metadata
			for (ModelAnnotationHandler handler : metadata) {
				handler.addDerivedMetadata();
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			throw new MeteorException("Error loading metadata for bundle:"+bundleName, e);
		}
	}

//	private void addAnnotationMetadata(List<Class<?>> classes, ModelRepository repository) {
//		for (Class<?> class1 : classes) {
//			Model model= class1.getAnnotation(Model.class);
//			if (model == null)
//				continue;
//			
//			Type type= repository.findType(class1);
//			Map<String, List<String>> annotationProperties= MeteorAnnotationUtils.getAllPropertyValues(model);
//			for (Object propertyURI : annotationProperties.keySet()) {
//				List<String> values= annotationProperties.get(propertyURI);
//				for (String value : values)
//					type.setProperty((String)propertyURI, value);
//			}
//			
//			Method[] methods= class1.getDeclaredMethods();
//			for (Method javaMethod : methods) {
//				model= javaMethod.getAnnotation(Model.class);
//				if (model == null) 
//					continue;
//				annotationProperties= MeteorAnnotationUtils.getAllPropertyValues(model);
//				if (annotationProperties.isEmpty())
//					continue;
//
//				String resourceURI= MeteorAnnotationUtils.isMeteorProperty(javaMethod) ? 
//					Meteor.getURIForProperty(javaMethod) :
//					Meteor.getURIForMethod(javaMethod);
//					
//				Resource resource= repository.findResourceByURI(resourceURI);
//				for (Object propertyURI : annotationProperties.keySet()) {
//					List<String> values= annotationProperties.get(propertyURI);
//					for (String value : values)
//						resource.setProperty((String)propertyURI, value);
//				}
//			}
//		}
//	}

//	public static void sortByDependencies(List<Class> p_moduleClasses) {
//		Comparator<Class> comparator= new Comparator<Class>() {
//			public int compare(Class c1, Class c2) {
//				if (OsgiMetadataDiscoverer.getDependencies(c1).contains(c2))
//					return 1;
//				if (OsgiMetadataDiscoverer.getDependencies(c2).contains(c1))
//					return -1;
//				return 0;
//			}
//		};
//		Collections.sort(p_moduleClasses, comparator);
//	}

	static List<ModelAnnotationHandler> findAnnotations(Collection<Class<?>> classes, Scope repository) {
		List<ModelAnnotationHandler> models= new ArrayList<ModelAnnotationHandler>();
		Map<Class<?>, List<ModelAnnotationHandler.Factory>> completedHandlerTypes= new HashMap<Class<?>, List<ModelAnnotationHandler.Factory>>();
		Map<Class<?>, List<ModelAnnotationHandler.Factory>> allHandlerTypes= Meteor.getAllModelAnnotationHandlers();
		while (!allHandlerTypes.isEmpty()) { // repeat as long as new annotation handlers are added
			for (Class<?> class1 : classes) {
				try {
					Annotation[] annotations= class1.getAnnotations();
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> annotationType= annotation.annotationType();
						List<ModelAnnotationHandler.Factory> handlerTypes= allHandlerTypes.get(annotationType);
						if (handlerTypes != null) {
							for (ModelAnnotationHandler.Factory handlerType : handlerTypes) {
								Annotation methodAnnotation= class1.getAnnotation(annotationType);
								ModelAnnotationHandler handler= handlerType.createAnnotationHandler();
								if (handler.initialize(repository, methodAnnotation, class1))
									models.add(handler);
							}
						}
					}
					
					
					Method[] methods= class1.getDeclaredMethods();
					for (Method method : methods) {
						annotations= method.getAnnotations();
						for (Annotation annotation : annotations) {
							Class<? extends Annotation> annotationType= annotation.annotationType();
							List<ModelAnnotationHandler.Factory> handlerTypes= allHandlerTypes.get(annotationType);
							if (handlerTypes != null) {
								for (ModelAnnotationHandler.Factory handlerType : handlerTypes) {
									Annotation methodAnnotation= method.getAnnotation(annotationType);
									ModelAnnotationHandler handler= handlerType.createAnnotationHandler();
									if (handler.initialize(repository, methodAnnotation, method))
										models.add(handler);
								}
							}
						}
					}
				} 
				catch (Exception e) {
					Logging.severe("Failed to instantiate a Model model", e);
					throw MeteorException.getMeteorException("Failed to instantiate a Model model", e);
				}
			}
			
			// detect any newly added handlers
			for (Class<?> annotationType : allHandlerTypes.keySet()) {
				List<ModelAnnotationHandler.Factory> oldHandlers= allHandlerTypes.get(annotationType);
				List<ModelAnnotationHandler.Factory> completedHandlers= completedHandlerTypes.get(annotationType);
				if (completedHandlers == null) {
					completedHandlers= new ArrayList<ModelAnnotationHandler.Factory>();
					completedHandlerTypes.put(annotationType, completedHandlers);
				}
				completedHandlers.addAll(oldHandlers);
			}
			Map<Class<?>, List<ModelAnnotationHandler.Factory>> newHandlerTypes= 
				new HashMap<Class<?>, List<ModelAnnotationHandler.Factory>>(Meteor.getAllModelAnnotationHandlers());
			for (Class<?> annotationType : newHandlerTypes.keySet()) 
				newHandlerTypes.put(annotationType, new ArrayList<ModelAnnotationHandler.Factory>(newHandlerTypes.get(annotationType)));
			for (Class<?> annotationType : completedHandlerTypes.keySet()) {
				List<ModelAnnotationHandler.Factory> oldHandlers= completedHandlerTypes.get(annotationType);
				List<ModelAnnotationHandler.Factory> newHandlers= newHandlerTypes.get(annotationType);
				newHandlers.removeAll(oldHandlers);
				if (newHandlers.isEmpty()) 
					newHandlerTypes.remove(annotationType);					
			}
			allHandlerTypes= newHandlerTypes;
		}
		return models;
	}

//	static void findModules(Collection<Class<?>> classes, ModelRepository repository) {
//		List<Class> moduleClasses= new ArrayList<Class>();
//		for (Class<?> class1 : classes) {
//			if (Module.class.isAssignableFrom(class1) == false)
//				continue;
//			if (class1.isAnnotationPresent(Model.class) == false)
//				continue;
//			
//			moduleClasses.add(class1);
//		}
//		
//		for (Class moduleClass : moduleClasses)
//			repository.addModule(DefaultRepositoryImpl.createModule((DefaultRepositoryImpl)repository, moduleClass));
//	}

//	static void findJavaTypes(Collection<Class<?>> classes, ModelRepository repository) {
//		List<Class> resourceClasses= new ArrayList<Class>();
//		for (Class<?> class1 : classes) {
//			if (class1.isAnnotationPresent(Model.class) == false)
//				continue;
//			
//			resourceClasses.add(class1);
//		}
//		
//		HashSet<Package> createdPackages= new HashSet<Package>();
//		Type.Constructor constructor= repository.getService(Type.Constructor.class);
//		Namespace.Constructor nsConstructor= repository.getService(Namespace.Constructor.class);
//		for (Class moduleClass : resourceClasses) {
//
//			// create namespace if needed
//			Package pkg= moduleClass.getPackage();
//			if (!createdPackages.contains(pkg)) {
//				Namespace ns= nsConstructor.create(pkg);
//				repository.addResource(ns);
//				createdPackages.add(pkg);
//			}
//			
//			// create Type
//			Type type= constructor.create(moduleClass);
//			repository.addResource(type);
//		}
//	}

	@SuppressWarnings("restriction")
	static List<URL> getBundleClasspathURLs(Bundle bundle) throws BundleException {
		List<URL> urls= new ArrayList<URL>();
		
		// declared classpath
		String headers = (String) bundle.getHeaders().get(
				Constants.BUNDLE_CLASSPATH);
		
		// TWS - OSGi spec says that if no classpath is specified then ./ is assumed
		if (headers == null || headers.length() <= 0) {
			headers="/";
		}
		
		boolean inDevelopmentMode= false;
		try {
			inDevelopmentMode= DevClassPathHelper.inDevelopmentMode();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	
		/*
		 * get declared bundle classpath
		 */
		if (inDevelopmentMode == false) {
			ManifestElement[] paths = 
				ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, headers);
			if (paths != null) {
				for (int i = 0; i < paths.length; i++) {
					try {
						String path = paths[i].getValue();
						URL url = bundle.getEntry(path);
						if (url != null) {
							urls.add(FileLocator.resolve(url));
						}
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			/*
			 * Get development classpath
			 * This is special support that gets paths used by Eclipse IDE when running 
			 * an OSGi application from the IDE. 
			 */
			String[] devpaths = DevClassPathHelper.getDevClassPath(bundle.getSymbolicName());
			if (devpaths != null) {
				for (int i = 0; i < devpaths.length; i++) {
					try {
						URL url = bundle.getEntry(devpaths[i]);
						if (url != null)
							urls.add(FileLocator.resolve(url));
					} 
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		}
		
		
		return urls;
	}

	private static void registerAnnotationHandlers(List<Class<?>> classes) {
		
		// register model annotation handlers
		for (final Class<?> handlerType : classes) {
			if (!handlerType.isAnnotationPresent(Model.class))
				continue;
			if (!ModelAnnotationHandler.class.isAssignableFrom(handlerType))
				continue;
			if (!handlerType.isAnnotationPresent(ProcessesAnnotations.class))
				continue;
			ProcessesAnnotations processesAnnotations= handlerType.getAnnotation(ProcessesAnnotations.class);
			Class<?>[] classes2= processesAnnotations.value();
			for (int i = 0; i < classes2.length; i++) {
				Class<?> annotationType = classes2[i];
				Meteor.registerModelAnnotationHandler(annotationType, new ModelAnnotationHandler.Factory() {
					@Override public ModelAnnotationHandler createAnnotationHandler()
					{
						try {
							return (ModelAnnotationHandler) handlerType.newInstance();
						}
						catch (Throwable t) {
							throw new MeteorException("Failed to create model annotation handler instance:"+handlerType.getName(), t);
						}
					}
				});
			}
		}
		
		
		// register injection annotation handlers
		for (final Class<?> handlerType : classes) {
			if (!handlerType.isAnnotationPresent(Model.class))
				continue;
			if (!InjectionAnnotationHandler.class.isAssignableFrom(handlerType))
				continue;
			if (!handlerType.isAnnotationPresent(ProcessesAnnotations.class))
				continue;
			ProcessesAnnotations processesAnnotations= handlerType.getAnnotation(ProcessesAnnotations.class);
			Class<?>[] classes2= processesAnnotations.value();
			for (int i = 0; i < classes2.length; i++) {
				Class<?> annotationType = classes2[i];
				Meteor.registerInjectionAnnotationHandler(annotationType, new InjectionAnnotationHandler.Constructor() {
					@Override public InjectionAnnotationHandler createAnnotationHandler()
					{
						try {
							return (InjectionAnnotationHandler) handlerType.newInstance();
						}
						catch (Throwable t) {
							throw new MeteorException("Failed to create injection annotation handler instance:"+handlerType.getName(), t);
						}
					}
				});
			}
		}		
		
	}

	private static List<Class<?>> loadClasses(Bundle bundle, Collection<String> names) {
		List<Class<?>> classes= new ArrayList<Class<?>>();
		for (String name : names) {
			try {
				Class<?> class1= bundle.loadClass(name);
				classes.add(class1);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return classes;
	}

	static List<String> findClasses(File file) {
		return (file.isDirectory()) ? 
				findClassesInDirectory(file) :
				findClassesInJar(file);
	}

	public static List<String> findClassesInJar(File file) {
		// TODO Unfinished
		throw new UnsupportedOperationException("Not implemented yet");
	}

	static List<String> findClassesInDirectory(File packageDirectory) {
		return findClassesInDirectory("",packageDirectory);
	}

	public static List<String> findClassesInDirectory(String packagePrefix, File packageDirectory) {
		List<String> classes= new ArrayList<String>();
	
		for (File childFile : packageDirectory.listFiles()) {
			if (childFile.isDirectory()) {
				String prefix= packagePrefix;
				if (0 < packagePrefix.length())
					prefix+= ".";
				prefix+= childFile.getName();
				classes.addAll(findClassesInDirectory(prefix, childFile));
			} 
			else {
				String name= childFile.getName();
				if (name.endsWith(".class")) {
					
					String clsName= packagePrefix;
					if (0 < packagePrefix.length())
						clsName+= ".";
					clsName+= name.substring(0, name.length() - 6);
					classes.add(clsName);
				}
			}
		}
		
		return classes;
	}

}
