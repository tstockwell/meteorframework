package com.googlecode.meteorframework.utils;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * An image registry maintains a mapping between symbolic image names 
 * and SWT image objects or special image descriptor objects which
 * defer the creation of SWT image objects until they are needed.
 * <p>
 * An image registry owns all of the image objects registered
 * with it, and automatically disposes of them when the SWT Display
 * that creates the fonts is disposed. Because of this, clients do not 
 * need to (indeed, must not attempt to) dispose of these images themselves.
 * </p>
 * <p>
 * Clients may instantiate this class (it was not designed to be subclassed).
 * </p>
 * <p>
 *
 * <p>
 * Unlike the FontRegistry, it is an error to replace images. As a result
 * there are no events that fire when values are changed in the registry
 * </p>
 */
@SuppressWarnings("unchecked")
public class ImageRegistry {
    
    /**
     * Table of known images keyed by symbolic image name
     * (key type: <code>String</code>, 
     *  value type: <code>Image</code>).
     */
	private Map table = new HashMap(10);
/**
 * Creates an empty image registry.
 * <p>
 * There must be an SWT Display created in the current 
 * thread before calling this method.
 * </p>
 */
public ImageRegistry() {
}
/**
 * Returns the image associated with the given key in this registry, 
 * or <code>null</code> if none.
 *
 * @param key the key
 * @return the image, or <code>null</code> if none
 */
public Image get(String key) {
    Object entry = table.get(key);
    if (entry == null) {
        return null;
    }
    if (entry instanceof Image) {
        return (Image)entry;
    }
    return null;
}
/**
 * Returns the icon associated with the given image key in this registry, 
 * or <code>null</code> if none.
 *
 * @param key the key
 * @return the icon, or <code>null</code> if none
 */
public Icon getIcon(String key) {
    String iconKey= key + ".icon";
    Object entry = table.get(iconKey);
    if (entry != null && entry instanceof Icon) 
        return (Icon)entry;
    entry = table.get(key);
    if (entry != null && entry instanceof Image) {
        Icon icon= new ImageIcon((Image)entry);
        table.put(iconKey, icon);
        return icon;
    }
    return null;
}
/**
 * Adds an image to this registry.  This method
 * fails if there is already an image with the given key.
 * <p>
 * Note that an image registry owns all of the image objects registered
 * with it, and automatically disposes of them the SWT Display is disposed. 
 * Because of this, clients must not register an image object
 * that is managed by another object.
 * </p>
 *
 * @param key the key
 * @param image the image
 * @exception IllegalArgumentException if the key already exists
 */
public void put(String key, Image image) {
    Object entry = table.get(key);
    if (entry == null || entry instanceof Image) {
        //replace with the new descriptor
        table.put(key, image);
        table.remove(key+".icon");
        return;
    }
    throw new IllegalArgumentException("ImageRegistry key already in use: " + key);//$NON-NLS-1$
}
}
