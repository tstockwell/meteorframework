package com.googlecode.meteorframework.core.annotation;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.regex.Pattern;

import com.googlecode.meteorframework.core.Scope;
import com.googlecode.meteorframework.core.utils.TurtleReader;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;

public class MeteorAnnotationUtils {

	public static final boolean isWild(String uri) {
		return 0 <= uri.indexOf('*');
	}
	
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

	public static void addMetadata(Scope scope, String turtle) 
	throws ParseException 
	{
		turtle= turtle.trim();
		if (!turtle.endsWith("."))
			turtle+= ".";
		TurtleReader reader= new TurtleReader(turtle);
		reader.addMetadataToScope(scope);
	}

}
