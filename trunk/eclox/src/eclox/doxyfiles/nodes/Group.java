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

package eclox.doxyfiles.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Implements the group node class.
 * 
 * @author gbrocker
 */
public class Group extends Node {
    
    /**
	 * The collection of chid nodes.
	 */
	private List children = new Vector();
	
	
	/**
     * A string containing the root node identifier.
     */
    public static final String ROOT = "ROOT";

    
    /**
     * Constructor.
     * 
     * @param	identifier a string containing the group identifier
     */
    public Group(String identifier) {
        super(identifier);
    }
    
    
    /**
     * @see eclox.doxyfiles.nodes.Node#accept(eclox.doxyfiles.nodes.IVisitor)
     */
    public void accept(IVisitor visitor) {
        visitor.process(this);
    }
    
    /**
	 * Adds the specified node to children.
	 * 
	 * @param	node	a node to add to the children
	 */
	public void addChild( Node node ) {
		this.children.add( node );
	}
	
	/**
	 * Retrieves an iterator on the child nodes.
	 * 
	 * @return	an itertor on child nodes
	 */
	public Iterator iterator() {
		return this.children.iterator();
	}
	
	/**
	 * Retreives the number of children of this node.
	 * 
	 * @return	an integer
	 */
	public int size() {
	    return this.children.size();
	}
	
	/**
	 * Retrieves all children of the node in an array.
	 * 
	 * @return	an array containing all children
	 */
	public Object[] toArray() {
	    return this.children.toArray();
	}

}
