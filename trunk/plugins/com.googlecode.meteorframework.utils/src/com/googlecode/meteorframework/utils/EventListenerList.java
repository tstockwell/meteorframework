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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * A class which holds a list of EventListeners.
 */
@SuppressWarnings("unchecked")
public class EventListenerList implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static class Entry implements Serializable
    {
		private static final long serialVersionUID = 1L;
		Class t;
        EventListener l;
        public Entry(Class t, EventListener l)
        {
            this.t = t;
            this.l = l;
        }
        public Class getListenerClass()
        {
            return t;
        }
        public EventListener getListener()
        {
            return l;
        }
        public boolean equals(Object o)
        {
            if (o instanceof Entry)
            {
                Entry e = (Entry) o;
                Class c = e.getClass();
                if (!c.equals(t))
                    return false;
                EventListener el = e.getListener();
                return el.equals(l);
            }
            return super.equals(o);
        }
    };

    /* A null array to be shared by all empty listener lists*/
    private final static Entry[] NULL_ARRAY = new Entry[0];
    /* The list of ListenerType - Listener pairs */
    protected transient Entry[] listenerList = NULL_ARRAY;

    public Entry[] getEntries()
    {
        return listenerList;
    }

    public synchronized EventListener[] getListeners(Class t)
    {
        ArrayList list = new ArrayList(listenerList.length);
        Entry[] lList = listenerList;
        for (int i = 0; i < lList.length; i++)
        {
            if (lList[i].getListenerClass().isAssignableFrom(t))
                list.add(lList[i].getListener());
        }
        EventListener[] el = new EventListener[list.size()];
        list.toArray(el);
        return el;
    }
    public synchronized EventListener[] getListeners()
    {
        EventListener[] el = new EventListener[listenerList.length];
        for (int i = 0; i < listenerList.length; i++)
            el[i] = listenerList[i].getListener();
        return el;
    }

    public synchronized Entry[] getEntries(Class t)
    {
        ArrayList list = new ArrayList(listenerList.length);
        Entry[] lList = listenerList;
        for (int i = 0; i < lList.length; i++)
        {
            if (t == lList[i].getListenerClass())
                list.add(lList[i]);
        }
        Entry[] el = new Entry[list.size()];
        list.toArray(el);
        return el;
    }

    /**
     * Return the total number of listeners for this listenerlist
     */
    public int getListenerCount()
    {
        return listenerList.length;
    }

    public boolean isEmpty()
    {
        return listenerList.length <= 0;
    }

    /**
     * Return the total number of listeners of the supplied type
     * for this listenerlist.
     */
    public int getListenerCount(Class t)
    {
        int count = 0;
        Entry[] lList = listenerList;
        for (int i = 0; i < lList.length; i++)
        {
            if (t == lList[i].getListenerClass())
                count++;
        }
        return count;
    }
    public final void add(Class t, EventListener l)
    {
        add(new Entry(t, l));
    }
    public final void add(EventListener listener)
    {
        add(listener.getClass(), listener);
    }
    /**
     * Add the listener as a listener of the specified type.
     * @param t the type of the listener to be added
     * @param l the listener to be added
     */
    public synchronized void add(Entry e)
    {
        Class t = e.getListenerClass();
        EventListener l = e.getListener();
        if (!t.isInstance(l))
        {
            throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
        }
        if (l == null)
        {
            throw new IllegalArgumentException("Listener " + l + " is null");
        }
        if (listenerList == NULL_ARRAY)
        {
            // if this is the first listener added,
            // initialize the lists
            listenerList = new Entry[] { e };
        }
        else
        {
            // Otherwise copy the array and add the new listener
            int i = listenerList.length;
            Entry[] tmp = new Entry[i + 1];
            System.arraycopy(listenerList, 0, tmp, 0, i);
            tmp[i] = e;
            listenerList = tmp;
        }
    }

    public final void remove(EventListener e)
    {
        remove(e.getClass(), e);
    }
    public final void remove(Entry e)
    {
        remove(e.getListenerClass(), e.getListener());
    }
    /**
     * Remove the listener as a listener of the specified type.
     * @param t the type of the listener to be removed
     * @param l the listener to be removed
     */
    public synchronized void remove(Class t, EventListener l)
    {
        if (!t.isInstance(l))
        {
            throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
        }
        if (l == null)
        {
            throw new IllegalArgumentException("Listener " + l + " is null");
        }

        // Is l on the list?
        int index = -1;
        for (int i = 0; i < listenerList.length; i++)
        {
            Entry e = listenerList[i];
            if ((e.getListenerClass() == t) && (e.getListener().equals(l) == true))
            {
                index = i;
                break;
            }
        }

        // If so,  remove it
        if (index != -1)
        {
            Entry[] tmp = new Entry[listenerList.length - 1];
            // Copy the list up to index
            System.arraycopy(listenerList, 0, tmp, 0, index);
            // Copy from two past the index, up to
            // the end of tmp (which is two elements
            // shorter than the old list)
            if (index < tmp.length)
                System.arraycopy(listenerList, index + 1, tmp, index, tmp.length - index);
            // set the listener array to the new array or null
            listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
        }
    }

    // Serialization support.
    private void writeObject(ObjectOutputStream s) throws IOException
    {
        Entry[] lList = listenerList;
        s.defaultWriteObject();

        // Save the non-null event listeners:
        for (int i = 0; i < lList.length; i++)
        {
            Entry e = lList[i];
            Class t = e.getListenerClass();
            EventListener l = e.getListener();
            if ((l != null) && (l instanceof Serializable))
            {
                s.writeObject(t.getName());
                s.writeObject(l);
            }
        }

        s.writeObject(null);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
    {
        listenerList = NULL_ARRAY;
        s.defaultReadObject();
        Object listenerTypeOrNull;

        while (null != (listenerTypeOrNull = s.readObject()))
        {
            EventListener l = (EventListener) s.readObject();
            add(Class.forName((String) listenerTypeOrNull), l);
        }
    }

    /**
     * Return a string representation of the EventListenerList.
     */
    public String toString()
    {
        Entry[] lList = listenerList;
        String s = "EventListenerList: ";
        s += lList.length + " listeners: ";
        for (int i = 0; i <= lList.length; i++)
        {
            Entry e = lList[i];
            s += " type " + e.getListenerClass().getName();
            s += " listener " + e.getListener();
        }
        return s;
    }
}
