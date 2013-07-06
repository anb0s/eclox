/*******************************************************************************
 * Copyright (C) 2003-2005, 2013, Guillaume Brocker
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * Implements a group of settings.
 * 
 * @author gbrocker
 */
public class Group {
    
    /**
     * a string containing the group name
     */
    private String name;
    
    /**
     * a collection of all settings in the group
     */
    private Collection settings = new Vector();
    
    /**
     * Constructor
     * 
     * @param   name    a string containing the group name
     */
    public Group( String name ) {
        this.name = new String( name );
    }
    
    /**
     * Addes a new setting in the group.
     * 
     * @param   setting a setting to add to the group
     */
    public void add( Setting setting ) {
        settings.add( setting );
    }
    
    /**
     * Retrieves the group name.
     * 
     * @return  a string containing the group name
     */
    public String getName() {
        return new String( this.name );
    }
    
    /**
     * Retrieves an iterator on all managed settings.
     * 
     * @return  an iterator
     */
    public Iterator iterator() {
        return settings.iterator();
    }

}
