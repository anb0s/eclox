/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2004 Guillaume Brocker

	This file is part of eclox.

    eclox is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    any later version.

    eclox is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with eclox; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.doxyfile.node;

import eclox.doxyfile.Doxyfile;
import eclox.util.ListenerManager;

/**
 * Implement the abstract node class.
 * 
 * @author gbrocker
 */
public abstract class Node extends ListenerManager {
	/**
	 * The doxyfile the node belongs to.
	 */
	private Doxyfile doxyfile;
	
	/**
	 * The node's dirty flag.
	 */
	private boolean dirty = false;
	
	/**
	 * Constructor.
	 * 
	 * @param	doxyfile	The doxyfile the node belongs to.
	 */
	public Node(Doxyfile doxyfile) {
		super(NodeListener.class);
		this.doxyfile = doxyfile;
	}
	 
	/**
	 * Implementation should feed the specified visitor with relevant informations.
	 * 
	 * @param visitor	The visitor to feed.
	 * 
	 * @exception	VisitorException	Thrown when the specified visitor encountered
	 * 									an error.
	 */
	public abstract void accept( Visitor visitor ) throws VisitorException;
	
	/**
	 * Add a new node listener.
	 * 
	 * @param listener	The listener to add.
	 */
	public void addNodeListener( NodeListener listener ) {
		super.addListener( listener );
	}
	
	/**
	 * Retrieves all children of the node.
	 * 
	 * @return	The collection of all children.
	 * 
	 * @author gbrocker
	 */
	public abstract java.util.Collection getChildren();
	
	/**
	 * Retrieves the node's doxyfile.
	 * 
	 * @return	A doxyfile instance.
	 */
	public Doxyfile getDoxyfile() {
		return this.doxyfile;
	}
	
	/**
	 * Tell if the node is dirty or note.
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * Remove the specified node listener.
	 * 
	 * @param listener	The node listener instance to remove.
	 */
	public void removeNodeListener( NodeListener listener ) {
		super.removeListener( listener );
	}
	
	/**
	 * Set the node as clean.
	 */
	public abstract void setClean(); 
	
	/**
	 * Converts the node to its text representation.
	 * 
	 * @return	A string containing the text representation of the node.
	 */
	public abstract String toString();
	
	/**
	 * Mark the node as dirty.
	 */
	protected void setDirtyInternal() {
		this.dirty = true;
		fireEvent( new NodeEvent( this ), "nodeDirty" );
	}
	
	/**
	 * Mark the node as clean.
	 */
	protected void setCleanInternal() {
		this.dirty = false;
		fireEvent( new NodeEvent( this ), "nodeClean" );
	}
}
