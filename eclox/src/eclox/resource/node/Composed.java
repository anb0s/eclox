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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * Implements the abstract doxyfile composed node class.
 * 
 * @author gbrocker
 */
public abstract class Composed extends Node implements Listener {
	/**
	 * The collection of chid nodes.
	 */
	private Collection children = new Vector();
	
	/**
	 * Adds the specified node to children.
	 * 
	 * @param	node	a node to add to the children
	 */
	public void addNode( Node node ) {
		node.addNodeListener( this );
		this.children.add( node );
		this.updateState( true );
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
	 * Removes the specified node from the children.
	 * 
	 * @param	node	a node to remove from the children
	 */
	public void removeNode( Node node ) {
		node.removeNodeListener( this );
		this.children.remove( node );
		this.updateState( true );
	}
	
	/**
	 * @see eclox.resource.node.Listener#nodeChanged(eclox.resource.node.Event)
	 */
	public void nodeChanged(Event event) {
		Iterator	iterator = this.iterator();
		boolean		dirty = false;
		
		while( iterator.hasNext() == true && dirty == false ) {
			Node	currentNode = (Node) iterator.next();
			
			dirty = currentNode.isDirty();
		}
		this.updateState( dirty );
	}
	
	/**
	 * @see eclox.resource.node.Node#clean()
	 */
	public void clean() {
		Iterator iterator = this.iterator();
		
		while( iterator.hasNext() == true ) {
			Node	currentNode = (Node) iterator.next();
			
			currentNode.clean();
		}
	}
}
