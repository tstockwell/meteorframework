package com.googlecode.meteorframework.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.osgi.framework.BundleException;

import com.googlecode.meteorframework.Meteor;
import com.googlecode.meteorframework.MeteorException;
import com.googlecode.meteorframework.MeteorNS;
import com.googlecode.meteorframework.Namespace;
import com.googlecode.meteorframework.Scope;
import com.googlecode.meteorframework.annotation.Decorates;
import com.googlecode.meteorframework.annotation.Decorator;
import com.googlecode.meteorframework.annotation.Inject;
import com.googlecode.meteorframework.annotation.MeteorAnnotationUtils;
import com.googlecode.meteorframework.annotation.Model;

@Decorator public abstract class NamespaceAdvice implements Namespace {
	

	@Decorator public static abstract class Constructor implements Namespace.Constructor 
	{
		@Inject Scope _repository;
		@Decorates Namespace.Constructor _self;
		
		@Override public Namespace create(String nsURI) {
			Namespace namespace= RepositoryImpl.findResourceByURI(_repository, nsURI, Namespace.class);
			if (namespace == null) {
				namespace= _repository.getInstance(Namespace.class);
				namespace.setURI(nsURI);
			}
			return namespace;
		}
		
		/**
		 * Creates a meteor type based on a Java type.
		 * Uses Java reflection to examine the Java class and create associated 
		 * properties and methods for the Meteor type.
		 * 
		 * The Java class is expected to follow Java Bean conventions.  
		 */
		@Override public Namespace create(Package javaType) {
			final String nsURI= Meteor.PROTOCOL+javaType.getName();
			try
			{
				Namespace namespace= RepositoryImpl.findResourceByURI(_repository, nsURI, Namespace.class);
				if (namespace == null) 
					namespace= _self.create(nsURI);
				
				// get dependencies form other bundles
				Set<String> dependentPackages= Activator.getMeteorClassloader().getAllDependentPackages(javaType.getName());
				Set<Namespace> dependentNamespaces= new HashSet<Namespace>();
				for (String dependentPackage : dependentPackages) {
					Namespace dependency= RepositoryImpl.findResourceByURI(_repository, dependentPackage, Namespace.class);
					if (dependency == null) 
						dependency= _self.create(dependentPackage);
					dependentNamespaces.add(dependency);
				}
				namespace.setDependencies(dependentNamespaces);
				
				
				// get dependencies in same bundle
				Model modelAnnotation= javaType.getAnnotation(Model.class);
				if (modelAnnotation != null) {
					List<String> dependencies= MeteorAnnotationUtils.getPropertyValues(modelAnnotation, MeteorNS.Namespace.dependencies);
					if (!dependencies.isEmpty()) {
						for (String packageURI : dependencies) {
							Namespace dependency= RepositoryImpl.findResourceByURI(_repository, packageURI, Namespace.class);
							if (dependency == null) 
								dependency= _self.create(packageURI);
							Set<Namespace> set= namespace.getDependencies();
							if (set == null) {  // can be null when Meteor is booting up
								set= new HashSet<Namespace>();
								namespace.setDependencies(set);
							}
							set.add(dependency);
						}
					}
				}
				
				return namespace;
			} 
			catch (BundleException e)
			{
				throw new MeteorException("Error creating namespace "+nsURI, e);
			}
		}
	}
	
	@Decorates private Namespace _self;
	
	@Override public int compareTo(Namespace namespace) {
		Set<Namespace> dependencies= _self.getDependencies();
		if (dependencies != null && dependencies.contains(namespace))
			return 1;
		dependencies= namespace.getDependencies();
		if (dependencies != null && dependencies.contains(namespace))
			return -1;
		return 0;
	}
}