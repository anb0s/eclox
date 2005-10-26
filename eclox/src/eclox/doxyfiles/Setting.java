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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;

import eclox.core.Plugin;
import eclox.core.Services;

/**
 * Implements the setting chunk.
 * 
 * @author gbrocker
 */
public class Setting extends Chunk {
    
	/**
     * The value pattern.
     */
    private static Pattern valuePattern = Pattern.compile("(?:(\"[^\"]*\"|[^\\s]+)\\s*)+");
    
    /**
     * The setting properties.
     */
    private static Properties properties;
    
    /**
     * A string containing the node identifier.
     */
    private final String identifier;
    
    /**
     * A collection with all attached listeners
     */
    private Set listeners = new HashSet();
    
    /**
     * The string containing the setting value.
     */
    private String value;
    
    
    /**
     * Defines the annotation node property.
     */
    public static final String NOTE = "note";
    
    /**
     * Defines the text node property name.
     */
    public static final String TEXT = "text";
    
    /**
     * Defines the type property name. 
     */
    public static final String TYPE = "type";
    
    
    /**
     * Retrieves the specified property given a setting.
     * 
     * @param   setting     a setting instance
     * @param   property    a string containing the name of the property to retrieve.
     * 
     * @return  a string containing the desired property value or null when
     *          no such property exists
     */
    private static String getPropertyValue( Setting setting, String property ) {
        // Ensures that properties have been loaded.
        if( properties == null ) {
            try {
                InputStream propertiesInput = Plugin.getDefault().openStream( new Path("misc/setting-properties.txt") );
             
                properties = new Properties();
                properties.load( propertiesInput );
            }
            catch( Throwable throwable ) {
                Services.showError( throwable );
            }            
        }
        
        // Searches for the desired property.
        String  propertyIdentifier = new String( setting.getIdentifier() + "." + property );
        return properties.getProperty( propertyIdentifier );
    }
    
    /**
     * Constructor.
     * 
     * @param	identifier	a string containing the setting identifier
     * @param	value		a string containing the setting value
     */
    public Setting(String identifier, String value) {
        this.identifier = new String(identifier);
        this.value = new String(value);
    }
    
    /**
     * Attaches a new setting listener instance.
     * 
     * @param	listener	a new setting listener instance
     */
    public void addSettingListener(ISettingListener listener) {
        this.listeners.add( listener );
    }
    
    /**
     * Retrieves the node identifier.
     * 
     * @return	a string containing the node identifier
     */
    public final String getIdentifier() {
        return this.identifier;
    }
    
    /**
     * Retrieves the value of the specified property.
     * 
     * @param   property    a string containing a property name
     * 
     * @return  a string containing the property value or null when the property was not found
     */
    public String getProperty( String property ) {
        return getPropertyValue( this, property );
    }
    
    /**
     * Retrieves the setting value.
     * 
     * @return	a string containing the setting value 
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Retrieves the splitted setting value.
     * 
     * @param collection	a collection instance that will receive the value parts
     * 
     * @return	the collection that received the value parts
     */
    public Collection getSplittedValue(Collection collection) {
        Matcher valueMatcher = valuePattern.matcher(value);
        if(valueMatcher.matches() == true) {
            for(int groupIndex = 1; groupIndex <= valueMatcher.groupCount(); ++groupIndex) {
                String value = valueMatcher.group(groupIndex);
                collection.add(value);
            }
        }
        return collection;
    }
    
    /**
     * Detaches a new setting listener instance.
     * 
     * @param	listener	a attached setting listener instance
     */
    public void removeSettingListener(ISettingListener listener) {
        this.listeners.remove( listener );
    }
    
    /**
     * Adds a new value to the setting
     *
     * @param	value	an object representing a value to add
     */
    public void setValue(String value) {
        // Assigns the new value. 
        this.value = new String(value);
        
        // Walks through the attached listeners and notify them.
        Iterator i = this.listeners.iterator();
        while( i.hasNext() == true ) {
            ISettingListener listener = (ISettingListener) i.next();
            listener.settingValueChanged( this );
        }            
    }
    
    public String toString() {
    	return this.identifier + " = " + this.value + "\n";
    }
    
}
