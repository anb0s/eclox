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

package eclox.resource.node;

import eclox.util.ListenerManager;

/**
 * Implements the abstract doxyfile node class.
 * 
 * @author gbrocker
 */
public abstract class Node {
	/**
	 * Tells if the node is dirty or not.
	 */
	private boolean dirty = false;
	
	/**
	 * The node listener manager.
	 */
	private ListenerManager listenerManager = new ListenerManager(Listener.class);
	
	/**
	 * Accepts the specified visitor.
	 * 
	 * @param visitor	a node visitor instance to accept
	 */
	public abstract void accept( Visitor visitor );
	
	/**
	 * Adds the specified node listener.
	 *
	 * @param	listener	a node listener instance to add
	 */
	public void addNodeListener( Listener listener ) {
		this.listenerManager.addListener(listener);
	}
	
	/**
	 * Removes the specified node listener
	 *
	 * @param	listener	a node listener instance to remove
	 */
	public void removeNodeListener( Listener listener ) {
		this.listenerManager.removeListener(listener);
	}
	
	/**
	 * Cleans the node.
	 */
	public abstract void clean();
	
	/**
	 * Tells if the node is dirty or not.
	 * 
	 * @return	true or false
	 */
	public boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * Updates the node state.
	 * 
	 *  @param	dirty	a boolean telling of the node is now dirty or not.
	 */
	protected void updateState( boolean dirty ) {
		if( dirty != this.dirty ) {
			this.dirty = dirty;
			this.listenerManager.fireEvent( new Event(this), "nodeChanged" );
		}
	}
}
