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

import java.util.ArrayList;
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
			m_dirtyChildren.remove( event.node );
			if( m_dirtyChildren.isEmpty() ) {
				setCleanInternal();
			}
		}
		
		/**
		 * Process a child dirty notification.
		 * 
		 * @param	event	The event to process.
		 */
		public void nodeDirty( NodeEvent event ) {
			m_dirtyChildren.add( event.node );
			setDirtyInternal();
		}	
	}
	
	/**
	 * The dirty child node count.
	 */
	private Set m_dirtyChildren = new HashSet();
	
	/**
	 * The child node collection.
	 */
	private java.util.Collection m_children = new ArrayList();
	
	/**
	 * Add a new child node.
	 * 
	 * @param child	The child node to add.
	 */
	public void addChild( Node child ) {
		m_children.add( child );
		child.addNodeListener( new ChildNodeListener() );
	}
	
	/**
	 * Retrieves all children of the node.
	 * 
	 * @return	The collection of all children.
	 * 
	 * @author gbrocker
	 */
	public java.util.Collection getChildren() {
		return new ArrayList( m_children );
	}
	
	/**
	 * Mark the group as clean.
	 */
	public void setClean() {
		if( isDirty() ) {
			Iterator	childPointer;
			
			childPointer = m_children.iterator();
			while( childPointer.hasNext() ) {
				Node	child;
				
				child = (Node) childPointer.next();
				child.setClean();
			}
		}
	} 
}
