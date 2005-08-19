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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements the setting node class.
 * 
 * @author gbrocker
 */
public class Setting {
    
	/**
     * The value pattern.
     */
    private static Pattern valuePattern = Pattern.compile("(?:(\"[^\"]*\"|[^\\s]+)\\s*)+");
    
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
    
}
