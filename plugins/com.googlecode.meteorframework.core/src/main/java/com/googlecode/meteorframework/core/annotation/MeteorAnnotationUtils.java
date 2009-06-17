package com.googlecode.meteorframework.core.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.googlecode.meteorframework.core.CoreNS;
import com.googlecode.meteorframework.core.utils.TurtleReader;
import com.googlecode.meteorframework.utils.Logging;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;

@SuppressWarnings("unchecked")
public class MeteorAnnotationUtils {

	public static final boolean isWild(String uri) {
		return 0 <= uri.indexOf('*');
	}
	
	private static HashMap<ModelElement, Map<String, List<String>>> __annotationProperties= new HashMap<ModelElement, Map<String, List<String>>>();

	/**
	 * 
	 * @param template  A URI that may include '*" as a wildcard. 
	 * @param target A normal URI to match to the wild URI
	 * @return true if the given literal URI matches the wild URI 
	 */
	public static boolean matches(String wildURI, String regularURI) {
		
		// first, escape any dots and question marks in the template.  
		wildURI= wildURI.replaceAll("\\.", "\\.");
		wildURI= wildURI.replaceAll("\\?", "\\?");
		
		// replace * with .* to make correct Java regular expression syntax		
		wildURI= wildURI.replaceAll("\\*", "\\.\\*");
		
        boolean found= Pattern.compile(wildURI).matcher(regularURI).find();
		return found;
	}

	public static boolean isMethodBinding(String fieldName, Class<?> javaType) 
	{
		boolean isMethodBinding= false;
		Method[] roleClassMethods= javaType.getDeclaredMethods();
		for (int j = 0; !isMethodBinding && j < roleClassMethods.length; j++) {
			if (roleClassMethods[j].getName().equals(fieldName)) 
				isMethodBinding= true;
		}
		return isMethodBinding;
	}

	public static List<String> getPropertyValues(ModelElement annotation, String type) {
		List<String> list= getAllPropertyValues(annotation).get(type);
		if (list == null)
			return Collections.EMPTY_LIST;
		return list;
	}

	public static Map<String, List<String>> getAllPropertyValues(ModelElement annotation) {
		Map<String, List<String>> properties= __annotationProperties.get(annotation);
		if (properties == null) {
			//TODO - will need to expand this parsing facility
			properties= new HashMap<String, List<String>>();
			Metadata[] settings= annotation.value();
			if (settings != null) { 
				for (Metadata setting : settings) {
					String turtle= "meteor:model-annotation";
					for (String string : setting.value())
						turtle+= " "+string;
					try {
						TurtleReader reader= new TurtleReader(turtle);
						List<TurtleReader.Statement> statements= reader.getStatements();
						for (TurtleReader.Statement statement : statements)
						{
							List<String> values= properties.get(statement.predicate);
							if (values == null) {
								values= new ArrayList<String>();
								properties.put(statement.predicate, values);
							}
							values.add(statement.object);
						}
					} catch (Exception e) {
						Logging.warning("Error parsing turtle-formatted metadata:"+turtle, e);
					}
				}
			}
			__annotationProperties.put(annotation, properties);
		}
		return properties;
	}

	/**
	 * Returns true if the given Java method is a setter or getter method for 
	 * a Meteor property.  
	 */
	public static boolean isMeteorProperty(MethodDeclaration methodDeclaration) {
		if (!methodDeclaration.getModifiers().contains(Modifier.PUBLIC))
			return false;
		if (methodDeclaration.getModifiers().contains(Modifier.STATIC))
			return false;
		
		if (methodDeclaration.getAnnotation(IsFunction.class) != null)
			return false;
		
		ModelElement model= methodDeclaration.getAnnotation(ModelElement.class);
		if (model != null) {
			List<String> type= MeteorAnnotationUtils.getPropertyValues(model, CoreNS.Resource.type);
			if (!type.isEmpty())
				return type.contains(CoreNS.Property.TYPE);
		}
		
		String methodName= methodDeclaration.getSimpleName();
		return methodName.startsWith("get") || methodName.startsWith("set") || methodName.startsWith("is");
	}
	
	/**
	 * Returns true if the given Java method is a setter or getter method for 
	 * a Meteor property.  
	 */
	public static boolean isMeteorProperty(Method methodDeclaration) {
		if (!java.lang.reflect.Modifier.isPublic(methodDeclaration.getModifiers()))
			return false;
		if (java.lang.reflect.Modifier.isStatic(methodDeclaration.getModifiers()))
			return false;
		
		if (methodDeclaration.getAnnotation(IsFunction.class) != null)
			return false;
		
		ModelElement model= methodDeclaration.getAnnotation(ModelElement.class);
		if (model != null) {
			List<String> type= MeteorAnnotationUtils.getPropertyValues(model, CoreNS.Resource.type);
			if (!type.isEmpty())
				return type.contains(CoreNS.Property.TYPE);
		}
		
		if (methodDeclaration.getAnnotation(IsFunction.class) != null)
			return true;
		
		String methodName= methodDeclaration.getName();
		if ((methodName.startsWith("get") || methodName.startsWith("set")) && 3 < methodName.length())
			return true;
		
		if (methodName.startsWith("is") && 2 < methodName.length())
			return true;
		
		return false;
	}
	
	/**
	 * Returns true if the given Java method implements a Meteor method.  
	 */
	public static boolean isMeteorMethod(Method methodDeclaration) {
		if (!java.lang.reflect.Modifier.isPublic(methodDeclaration.getModifiers()))
			return false;
		if (java.lang.reflect.Modifier.isStatic(methodDeclaration.getModifiers()))
			return false;
		ModelElement model= methodDeclaration.getAnnotation(ModelElement.class);
		if (model != null) {
			List<String> type= MeteorAnnotationUtils.getPropertyValues(model, CoreNS.Resource.type);
			if (!type.isEmpty())
				return type.contains(CoreNS.Function.TYPE);
		}
		
		if (methodDeclaration.getAnnotation(IsFunction.class) != null)
			return true;
		
		String methodName= methodDeclaration.getName();
		if ((methodName.startsWith("get") || methodName.startsWith("set")) && 3 < methodName.length())
			return false;
		if (methodName.startsWith("is") && 2 < methodName.length())
			return false;
		
		return true;
	}

	public static boolean isModeledObject(Class<?> javaType)
	{
		if (javaType == null)
			return false;
		return javaType.isAnnotationPresent(ModelElement.class);
	}

}
