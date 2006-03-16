/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2005 Guillaume Brocker
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
