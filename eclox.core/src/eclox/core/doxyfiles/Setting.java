/*******************************************************************************
 * Copyright (C) 2003-2004, 2013 Guillaume Brocker
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/ 

package eclox.core.doxyfiles;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eclox.core.Plugin;
import eclox.core.doxyfiles.Chunk;

/**
 * Implements the setting chunk.
 * 
 * @author gbrocker
 */
public class Setting extends Chunk {
    
	/**
     * The value pattern.
     */
    private static Pattern valuePattern = Pattern.compile("([^ \"]+)\\s*|\"((\\\\\"|.)*?)\"\\s*");
    
    /**
     * The default setting properties.
     */
    private static Properties defaultProperties;
    
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
     * The setting local properties.
     */
    private Properties properties = new Properties();
    
    
    /**
     * Defines the group property name.
     */
    public static final String GROUP = "group";
    
    /**
     * Defines the note property name.
     */
    public static final String NOTE = "note";
    
    /**
     * Defines the text property name.
     */
    public static final String TEXT = "text";
    
    /**
     * Defines the type property name. 
     */
    public static final String TYPE = "type";
    
    
    /**
     * Initializes all setting default properties.
     */
    private static void initDefaultProperties() {
        // Ensures that properties have been loaded.
        if( defaultProperties == null ) {
            try {
                InputStream propertiesInput = Plugin.class.getResourceAsStream("/misc/setting-properties.txt");             
                
                defaultProperties = new Properties();
                defaultProperties.load( propertiesInput );
            }
            catch( Throwable throwable ) {
                Plugin.log( throwable );
            }            
        }
    }
    
    /**
     * Retrieves the specified default property given a setting.
     * 
     * @param   setting     a setting instance
     * @param   property    a string containing the name of the property to retrieve.
     * 
     * @return  a string containing the desired property value or null when
     *          no such property exists
     */
	private static String getDefaultPropertyValue( Setting setting, String property ) {
		initDefaultProperties();
		String	propertyIdentifier = setting.getIdentifier() + "." + property;
        return defaultProperties.getProperty( propertyIdentifier );
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
    		if( properties.containsKey( property) ) {
    			return properties.getProperty( property );
    		}
    		else {
    			return getDefaultPropertyValue( this, property );
    		}
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
        while( valueMatcher.find() == true ) {
        		String	value = valueMatcher.group( 1 );
        		if( value == null ) {
        			value = valueMatcher.group( 2 ).trim();
        		}
        		collection.add( value );
        }
        return collection;
    }
    
    /**
     * Tells if the setting has the givgen property set.
     * 
     * @param	property		a string containing a property name
     * 
     * @return	a boolean
     */
    public boolean hasProperty( String property ) {
    		if( properties.containsKey( property) ) {
			return true;
		}
		else {
			return getDefaultPropertyValue( this, property ) != null;
		}
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
     * Updates the value of a given property.
     * 
     * @param	property	a string containing the name of property
     * @param	value		a string containing the property value
     */
	public void setProperty( String property, String value ) {
		// Updates the given property.
		properties.setProperty( property, value );
		
        // Walks through the attached listeners and notify them.
        Iterator i = this.listeners.iterator();
        while( i.hasNext() == true ) {
        		ISettingListener		listener = (ISettingListener) i.next();
        		if( listener instanceof ISettingPropertyListener ) {
        			ISettingPropertyListener propertyListener = (ISettingPropertyListener) listener;
        			propertyListener.settingPropertyChanged( this, property );
        		}
        }
	}
	
	/**
	 * Removes the given property for the setting.
	 * 
	 * @param	property		a string containing a property name
	 */
	public void removeProperty( String property ) {
		if( properties.containsKey( property ) ) {
			properties.remove( property );
			
	        // Walks through the attached listeners and notify them.
	        Iterator i = this.listeners.iterator();
	        while( i.hasNext() == true ) {
	        		ISettingListener		listener = (ISettingListener) i.next();
	        		if( listener instanceof ISettingPropertyListener ) {
	        			ISettingPropertyListener propertyListener = (ISettingPropertyListener) listener;
	        			propertyListener.settingPropertyRemoved( this, property );
	        		}
	        }
		}
	}
    
    /**
     * Updates the value of the setting.
     *
     * @param	value	a string representing a value to set
     */
    public void setValue(String value) {
        this.value = new String(value);
        fireValueChangedEvent();
    }
    
    /**
     * Updates the value of the string with the given object collection. All objects
     * of the given collection will be converted to strings.
     * 
     * @param compounds	a collection of objects representing compounds of the new value
     */
    public void setValue( Collection compounds ) {
    		// Resets the managed value string.
		value = new String();
		
		// Walks through the comounds to rebuild the value.
		Iterator		i = compounds.iterator();
		while( i.hasNext() ) {
			// Retrieves the current compound.
			String	compound = i.next().toString(); 
			
			// Removes any leading and tralling spaces and manage the insertion.
			compound = compound.trim();
			if( compound.length() == 0 ) {
				continue;
			}
			else if( compound.indexOf(' ') != -1 ) {
				value = value.concat( "\"" + compound + "\" " );
			}
			else {
				value = value.concat( compound + " " );
			}
		}
		
		// Notifies all observers.
		fireValueChangedEvent();
    }
    
    public String toString() {
    		return this.identifier + " = " + this.value + "\n";
    }    
    
    /**
     * Notifies all observers that the setting value has changed.
     */
    private void fireValueChangedEvent() {
		Iterator i = listeners.iterator();
		while( i.hasNext() == true ) {
			ISettingListener		listener = (ISettingListener) i.next();
			if( listener instanceof ISettingValueListener ) {
				ISettingValueListener valueListener = (ISettingValueListener) listener;
				valueListener.settingValueChanged( this );
			}
		}	
    }

}
