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

package eclox.resource.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Implement a group node class.
 * 
 * @author gbrocker
 */
public abstract class Group extends Node {
	/**
	 * Implement a child node listener.
	 */
	private class ChildNodeListener implements NodeListener {
		/**
		 * Process a child clean notification.
		 * 
		 * @param	event	The event to process. 
		 */
		public void nodeClean( NodeEvent event ) {
			dirtyChildren.remove( event.node );
			if( dirtyChildren.isEmpty() ) {
				setCleanInternal();
			}
		}
		
		/**
		 * Process a child dirty notification.
		 * 
		 * @param	event	The event to process.
		 */
		public void nodeDirty( NodeEvent event ) {
			dirtyChildren.add( event.node );
			setDirtyInternal();
		}	
	}
	
	/**
	 * The dirty child node count.
	 */
	private Set dirtyChildren = new HashSet();
	
	/**
	 * The child node collection.
	 */
	private Collection children = new ArrayList();
	
	/**
	 * Add a new child node.
	 * 
	 * @param child	The child node to add.
	 */
	public void addChild(Node child) {
		this.children.add(child);
		child.addNodeListener(new ChildNodeListener());
	}
	
	/**
	 * Add a collection of childern to the group.
	 */
	public void addChildren(Collection children) {
		Iterator	it = children.iterator();
		
		while(it.hasNext() == true) {
			this.addChild((Node) it.next());
		}
	}
	/**
	 * Retrieves all children of the node.
	 * 
	 * @return	The collection of all children.
	 * 
	 * @author gbrocker
	 */
	public java.util.Collection getChildren() {
		return new ArrayList(children);
	}
	
	/**
	 * Mark the group as clean.
	 */
	public void setClean() {
		if(isDirty()) {
			Iterator	childPointer;
			
			childPointer = this.children.iterator();
			while( childPointer.hasNext() ) {
				Node	child;
				
				child = (Node) childPointer.next();
				child.setClean();
			}
		}
	} 
}
