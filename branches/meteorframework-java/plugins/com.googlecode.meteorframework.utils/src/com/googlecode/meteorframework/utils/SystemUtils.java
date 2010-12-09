package com.googlecode.meteorframework.utils;

/*
 * org.jlense.util plugin 
 * JLense Application Framework
 * http://jlense.sourceforge.net
 * 
 * Copyright (C) 2002 Ted Stockwell, et al.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name JLense nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission. 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *  Miscellaneouse application utilities
 *
 *  @author ted stockwell
 */
@SuppressWarnings("unchecked")
public class SystemUtils
{

	private static ImageRegistry __imageRegistry;

	public static ImageRegistry getImageRegistry() {
	    if (__imageRegistry == null)
	        __imageRegistry = new ImageRegistry();
	    return __imageRegistry;
	}
	
	
	
	
    /**
     * creates a map of default properties from static class variable definitions.
     * Assumes definitions of the form...
     *  static public String KEY_XXXXX= "...";
     *  static public String DEFAULT_XXXXX= "...";
     */
	public static Map getDEFAULTS(Class aClass)
    {
        Map DEFAULTS = new HashMap();
        Field[] fields = aClass.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            if (Modifier.isStatic(field.getModifiers()))
            {
                String name = field.getName();
                if (name.startsWith("DEFAULT_"))
                {
                    String keyName = "KEY_" + name.substring(8);
                    try
                    {
                        Field keyField = aClass.getField(keyName);
                        if (keyField != null)
                        {
                            try
                            {
                                Object keyValue = keyField.get(null);
                                Object defaultValue = field.get(null);
                                DEFAULTS.put(keyValue, defaultValue);
                            }
                            catch (IllegalAccessException x)
                            {
                            }
                        }
                    }
                    catch (NoSuchFieldException x)
                    {
                    }
                }
            }
        }
        return DEFAULTS;
    }

    /**
     *  Add command line definitions of the type -D<key>=<value> to a
     *  Properties collection.
     */
    static public void addDefines(Properties props, String[] args)
    {
        String nl = System.getProperty("line.separator");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < args.length; i++)
        {
            String key = args[i];
            if (key.startsWith("-D"))
            {
                buffer.append(key.substring(2));
                buffer.append(nl);
            }
        }

        // read in the properties
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.toString().getBytes());
        try
        {
            props.load(in);
        }

        // not gonna happen
        catch (IOException e)
        {
        }
    }

    /**
     *  Add command line definitions of the type -D<key>=<value> to a
     *  the System properties.
     */
    static public void addDefines(String[] args)
    {
        Properties sysProperties = System.getProperties();
        addDefines(sysProperties, args);
        System.setProperties(sysProperties);
    }

    static public String getStackTrace(Throwable t)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    /**
    *   Returns the URL associated with the named resource.
    *   Just like calling
    *   source.getClass().getClassLoader().getResource(name) except
    *   that you wont get NullPointerExceptions on JDK 1.1.x
    */
    static public URL getResource(Object source, String name)
    {
        URL resource = null;
        ClassLoader l = null;
        Class klass = null;
        if (source != null && (source instanceof ClassLoader))
        {
            l = (ClassLoader) source;
        }
        if (l == null && source != null && (source instanceof Class))
            l = (klass = (Class) source).getClassLoader();
        if (l == null && source != null)
            l = (klass = source.getClass()).getClassLoader();
        /*
        try finding the URL of the resource using the designated source (if any)
        */
        if (l != null)
            resource = l.getResource(name);
        /*
        if no source was designated or no resource was associated with the 
        source then try the system classloader
        */
        if (resource == null)
            resource = ClassLoader.getSystemResource(name);

        /*
        if still no resource was found then try appending the source package name to 
        the resource
        */
        if (resource == null && klass != null)
        {
            if (!name.startsWith("/"))
                name = "/" + name;
            String prefix = klass.getName();
            int i = prefix.lastIndexOf('.');
            if (0 <= i)
            {
                prefix = prefix.substring(0, i);
                prefix = prefix.replace('.', '/');
                name = prefix + name;
                if (l != null)
                    resource = l.getResource(name);
                if (resource == null)
                    resource = ClassLoader.getSystemResource(name);
            }
        }

        return resource;
    }
    static public URL getResource(String name)
    {
        return getResource(null, name);
    }
    static public Image createImage(Object source, String name)
    {
        URL url = getResource(source, name);
        if (url == null)
            return null;
        return Toolkit.getDefaultToolkit().createImage(url);
    }

    static public Properties getSystemProperties(String name) throws IOException
    {
        InputStream in = ClassLoader.getSystemResourceAsStream(name);
        Properties properties = new Properties();
        properties.load(in);
        try
        {
            in.close();
        }
        catch (Throwable t)
        {
        }
        return properties;
    }

    /**
    *   Parses an array of strings as if they were command-line
    *   arguments.
    *   Elements that start with - are considered "keys".  An element
    *   in the array that followes a key and does not start with a
    *   '-' is considered a "value".
    */
    static public Map parseArgs(String[] args)
    {
        Map map = new HashMap(args.length);
        String key = null;
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (arg.startsWith("-"))
            {
                key = arg;
                map.put(arg, NO_VALUE);
            }
            else
            {
                if (key == null)
                {
                    map.put(arg, NO_VALUE);
                }
                else
                {
                    map.put(key, arg);
                    key = null;
                }
            }
        }
        return map;
    }
    /**
     * Same as parseArgs(String[] args) except that the given String is first 
     * tokenized into a String array.
     */
    static public Map parseArgs(String args)
    {
        StringTokenizer tokenizer = new StringTokenizer(args, " \r\n\t");
        int count = tokenizer.countTokens();
        String[] aArgs = new String[count];
        for (int i = 0; i < count; i++)
            aArgs[i] = tokenizer.nextToken();
        return parseArgs(aArgs);
    }
    static final String NO_VALUE = "";

    /** Operating system is Windows NT. */
    public static final int OS_WINNT = 1;
    /** Operating system is Windows 95. */
    public static final int OS_WIN95 = 2;
    /** Operating system is Windows 98. */
    public static final int OS_WIN98 = 4;
    /** Operating system is Solaris. */
    public static final int OS_SOLARIS = 8;
    /** Operating system is Linux. */
    public static final int OS_LINUX = 16;
    /** Operating system is HP-UX. */
    public static final int OS_HP = 32;
    /** Operating system is IBM AIX. */
    public static final int OS_AIX = 64;
    /** Operating system is SGI IRIX. */
    public static final int OS_IRIX = 128;
    /** Operating system is Sun OS. */
    public static final int OS_SUNOS = 256;
    /** Operating system is Compaq TRU64 Unix */
    public static final int OS_TRU64 = 512;
    /** @deprecated please use OS_TRU64 instead */
    public static final int OS_DEC = OS_TRU64;
    /** Operating system is OS/2. */
    public static final int OS_OS2 = 1024;
    /** Operating system is Mac. */
    public static final int OS_MAC = 2048;
    /** Operating system is Windows 2000. */
    public static final int OS_WIN2000 = 4096;
    /** Operating system is Compaq OpenVMS */
    public static final int OS_VMS = 8192;
    /** Operating system is unknown. */
    public static final int OS_OTHER = 65536;

    /** A mask for Windows platforms. */
    public static final int OS_WINDOWS_MASK = OS_WINNT | OS_WIN95 | OS_WIN98 | OS_WIN2000;
    /** A mask for Unix platforms. */
    public static final int OS_UNIX_MASK = OS_SOLARIS | OS_LINUX | OS_HP | OS_AIX | OS_IRIX | OS_SUNOS | OS_TRU64 | OS_MAC;

    /** Get the operating system on which the IDE is running.
    * @return one of the <code>OS_*</code> constants (such as {@link #OS_WINNT})
    */
    public static final int getOperatingSystem()
    {
        if (operatingSystem == -1)
        {
            String osName = System.getProperty("os.name");
            if ("Windows NT".equals(osName)) // NOI18N
                operatingSystem = OS_WINNT;
            else if ("Windows 95".equals(osName)) // NOI18N
                operatingSystem = OS_WIN95;
            else if ("Windows 98".equals(osName)) // NOI18N
                operatingSystem = OS_WIN98;
            else if ("Windows 2000".equals(osName)) // NOI18N
                operatingSystem = OS_WIN2000;
            else if ("Solaris".equals(osName)) // NOI18N
                operatingSystem = OS_SOLARIS;
            else if (osName.startsWith("SunOS")) // NOI18N
                operatingSystem = OS_SOLARIS;
            else if ("Linux".equals(osName)) // NOI18N
                operatingSystem = OS_LINUX;
            else if ("HP-UX".equals(osName)) // NOI18N
                operatingSystem = OS_HP;
            else if ("AIX".equals(osName)) // NOI18N
                operatingSystem = OS_AIX;
            else if ("Irix".equals(osName)) // NOI18N
                operatingSystem = OS_IRIX;
            else if ("SunOS".equals(osName)) // NOI18N
                operatingSystem = OS_SUNOS;
            else if ("Digital UNIX".equals(osName)) // NOI18N
                operatingSystem = OS_TRU64;
            else if ("OS/2".equals(osName)) // NOI18N
                operatingSystem = OS_OS2;
            else if ("OpenVMS".equals(osName)) // NOI18N
                operatingSystem = OS_VMS;
            else if (osName.equals("Mac OS X")) // NOI18N
                operatingSystem = OS_MAC;
            else if (osName.startsWith("Darwin")) // NOI18N
                operatingSystem = OS_MAC;
            else
                operatingSystem = OS_OTHER;
        }
        return operatingSystem;
    }

    /** Test whether the IDE is running on some variant of Windows.
    * @return <code>true</code> if Windows, <code>false</code> if some other manner of operating system
    */
    public static final boolean isWindows()
    {
        return (getOperatingSystem() & OS_WINDOWS_MASK) != 0;
    }

    /** Test whether the IDE is running on some variant of Unix.
    * Linux is included as well as the commercial vendors.
    * @return <code>true</code> some sort of Unix, <code>false</code> if some other manner of operating system
    */
    public static final boolean isUnix()
    {
        return (getOperatingSystem() & OS_UNIX_MASK) != 0;
    }

    private static int operatingSystem = -1;


}
