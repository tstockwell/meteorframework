/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.googlecode.meteorframework.utils;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class copied from org.eclipse.osgi.framework.internal.defaultadaptor
 */
@SuppressWarnings("unchecked")
public class DevClassPathHelper {
	static protected boolean inDevelopmentMode = false;
	static protected String[] devDefaultClasspath;
	static protected Properties devProperties = null;

	static {
		// Check the osgi.dev property to see if dev classpath entries have been
		// defined.
		String osgiDev = System.getProperty("osgi.dev"); //$NON-NLS-1$
		if (osgiDev != null) {
			try {
				inDevelopmentMode = true;
				URL location = new URL(osgiDev);
				devProperties = load(location);
				if (devProperties != null)
					devDefaultClasspath = getArrayFromList(devProperties
							.getProperty("*")); //$NON-NLS-1$
			} catch (MalformedURLException e) {
				devDefaultClasspath = getArrayFromList(osgiDev);
			}
		}
	}

	public static String[] getDevClassPath(String id) {
		String[] result = null;
		if (id != null && devProperties != null) {
			String entry = devProperties.getProperty(id);
			if (entry != null)
				result = getArrayFromList(entry);
		}
		if (result == null)
			result = devDefaultClasspath;
		return result;
	}

	/**
	 * Returns the result of converting a list of comma-separated tokens into an
	 * array
	 * 
	 * @return the array of string tokens
	 * @param prop
	 *            the initial comma-separated string
	 */
	public static String[] getArrayFromList(String prop) {
		if (prop == null || prop.trim().equals("")) //$NON-NLS-1$
			return new String[0];
		Vector list = new Vector();
		StringTokenizer tokens = new StringTokenizer(prop, ","); //$NON-NLS-1$
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			if (!token.equals("")) //$NON-NLS-1$
				list.addElement(token);
		}
		return list.isEmpty() ? new String[0] : (String[]) list
				.toArray(new String[list.size()]);
	}

	public static boolean inDevelopmentMode() {
		return inDevelopmentMode;
	}

	/*
	 * Load the given properties file
	 */
	private static Properties load(URL url) {
		Properties props = new Properties();
		try {
			InputStream is = null;
			try {
				is = url.openStream();
				props.load(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			// TODO consider logging here
		}
		return props;
	}
}