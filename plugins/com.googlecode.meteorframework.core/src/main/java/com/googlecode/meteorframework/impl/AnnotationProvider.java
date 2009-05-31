
package com.googlecode.meteorframework.impl;

/**
 * Supports instantiation of annotation types.
 */
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.meteorframework.MeteorException;


@SuppressWarnings("unchecked")
public abstract class AnnotationProvider
{

	public static <T extends Annotation> T createAnnotation(Class<T> annotationType, Map<String, Object> values) 
	{
		Object instance= Proxy.newProxyInstance(
				Activator.getMeteorClassloader(), 
				new Class<?>[] { annotationType }, 
				new AnnotationHandler(annotationType, values));
		return (T)instance;
	}

	static class AnnotationHandler implements Annotation, InvocationHandler {

		private Class annotationType;
		private Method[] members;
		Map<String, Object> values;


		AnnotationHandler(Class annotationType, Map<String, Object> values)
		{
			this.annotationType = annotationType;
			this.members = annotationType.getDeclaredMethods();
			this.values= values;
			if (this.values == null)
				this.values= new HashMap<String, Object>();
			setDefaultValues();
		}
		
		private void setDefaultValues() 
		{
			for (Method method : members) 
			{
				Object defaultValue= method.getDefaultValue();
				if (defaultValue != null) 
					if (!values.containsKey(method.getName()))
						values.put(method.getName(), defaultValue);
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable
		{
			if (annotationType.equals(method.getDeclaringClass()))
				return values.get(method.getName());
			return call(method, this, args); 
		}  

		public Class<? extends Annotation> annotationType()
		{
			return annotationType;
		}

		@Override
		public String toString()
		{
			String string = "@" + annotationType().getName() + "(";
			for (int i = 0; i < members.length; i++)
			{
				string += members[i].getName() + "=";
				string += call(members[i], this, null);
				if (i < members.length - 1)
				{
					string += ",";
				}
			}
			return string + ")";
		}

		@Override
		public boolean equals(Object other)
		{
			if (other instanceof Annotation)
			{
				Annotation that = (Annotation) other;
				if (this.annotationType().equals(that.annotationType()))
				{
					for (Method member : members)
					{
						Object thisValue = call(member, this, null);
						Object thatValue = call(member, that, null);
						if (thisValue == null)
							if (thatValue != null)
								return false;
						if (!thisValue.equals(thatValue))
							return false;
					}
					return true;
				}
			}
			return false;
		}

		@Override
		/*
		 * The hash code of a primitive value v is equal to WrapperType.valueOf(v).hashCode(), where WrapperType is the wrapper type corresponding to the primitive type of v (Byte, Character, Double, Float, Integer, Long, Short, or Boolean).
		 * The hash code of a string, enum, class, or annotation member-value I v is computed as by calling v.hashCode(). (In the case of annotation member values, this is a recursive definition.)
		 * The hash code of an array member-value is computed by calling the appropriate overloading of Arrays.hashCode on the value. (There is one overloading for each primitive type, and one for object reference types.)
		 */
		public int hashCode()
		{
			int hashCode = 0;
			for (Method member : members)
			{
				int memberNameHashCode = 127 * member.getName().hashCode();
				int memberValueHashCode = call(member, this, null).hashCode();
				hashCode += memberNameHashCode ^ memberValueHashCode;
			}       
			return hashCode;
		}

		private static Object call(Method method, Object instance, Object[] args)
		{
			try
			{
				return method.invoke(instance, args);
			}
			catch (Throwable e)
			{
				throw new MeteorException("Internal Error calling " + method.getName() + " on " + method.getDeclaringClass(), e);
			}
		}
	}
}
