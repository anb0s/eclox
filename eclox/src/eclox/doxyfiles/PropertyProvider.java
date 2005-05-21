/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2004 Guillaume Brocker
 *
 * This file is part of eclox.
 *
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
 */

package eclox.doxyfiles;

import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.runtime.Path;

import eclox.core.Plugin;
import eclox.core.Services;
import eclox.doxyfiles.io.PropertyReader;

/**
 * Defines the default node description provider.
 * 
 * @author gbrocker
 */
public class PropertyProvider {
    
    /**
     * Defines the node property class.
     */
    private class Properties extends java.util.HashMap {     
    };
    
    
    /**
     * The singleton instance of the node description provider.
     */
    private static PropertyProvider instance = new PropertyProvider();
    
    
    /**
     * The hash map containing all node properties.
     */
    private HashMap propertyContainer = new HashMap();
    
    
    /**
     * Defines the annotation node property.
     */
    public static final String ANNOTATION = "annotation";
    
    /**
     * Defines the text node property name.
     */
    public static final String TEXT = "text";
    
    /**
     * Defines the type property name. 
     */
    public static final String TYPE = "type";
    
    /**
     * Retrieves the global instance of the description provider.
     * 
     * @return	a description provider instance.
     */
    public static PropertyProvider getDefault() {
        return instance;
    }
    
    
    /**
     * Constructor.
     */
    public PropertyProvider() {
        try {
            InputStream input = Plugin.getDefault().openStream(new Path("misc/node-properties.txt"));
            PropertyReader reader = new PropertyReader(input);
            
            reader.read(this);
        }
        catch(Throwable throwable) {
            Services.showError(throwable);
        }
    }
    
    /**
     * Retrieves the annotation for the specified node.
     * 
     * @param	node	a node instance for which an annotation must be found
     * 
     * @return	a string containing the node's annotation or null if none
     */
    public String getAnnotation(Setting node) {
        return getProperty(node, ANNOTATION);
    }
    
    /**
     * Retrives a node property value.
     * 
     * @param	node		a node instance
     * @param	property	a string containing a property name
     * 
     * @return	a string containing the property value or null when such
     * 			node property exists
     */
    public String getProperty(Setting node, String property) {
        String result;
        Properties properties = (Properties) propertyContainer.get(node.getIdentifier());
        if(properties == null) {
            result = null;
            Services.logWarning(node.getIdentifier() + ": no text property found for that node identifier.");
        }
        else {
            result = (String) properties.get(property); 
        }
        return result;  
    }
    
    /**
     * Retrieves the text for the specified node.
     * <br>
     * The text is a human readable string of the node identifier.
     * 
     * @param	node	a node instance for which a text must be found
     * 
     * @return	a string containing the node's text or null if none
     */
    public String getText(Setting node) {
        return getProperty(node, TEXT);
    }
    
    /**
     * Retrieves the type property of a node
     * 
     * @param node	a node instance
     * 
     * @return	a string containing the node's type or null when none
     */
    public String getType(Setting setting) {
        return getProperty(setting, TYPE);
    }
    
    /**
     * Adds the specified property for the specified setting.
     * 
     * @param	setting		a string containing a setting identifier
     * @param	property	a string containing a setting property name
     * @param	value		a string containing a setting property value
     */
    public void setProperty(String setting, String property, String value) {
        // Retrives the properties of the specified setting.
        Properties properties = (Properties) propertyContainer.get(setting);
        if(properties == null) {
            properties = new Properties();
            propertyContainer.put(setting, properties);
        }
        
        // Update the value of the specified property.
        properties.put(property, new String(value));
    }
    
}
