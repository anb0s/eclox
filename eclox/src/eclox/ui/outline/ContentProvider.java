/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003 Guillaume Brocker

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


package eclox.ui.outline;


import java.util.*;
import eclox.doxyfile.node.*;

/**
 * Implements the content provider for the outline tree view.
 * 
 * @author gbrocker
 */
public class ContentProvider implements org.eclipse.jface.viewers.ITreeContentProvider {

	public void dispose() {
	}
	
	public void inputChanged( org.eclipse.jface.viewers.Viewer viewer, Object oldInput, Object newInput ){
	}
	
	public Object[] getElements( Object inputElement ) {
		return getGroupChildren( inputElement );
	}
	
	public Object[] getChildren( Object object ) {
		return getGroupChildren( object );
	}
	
	public Object getParent( Object object ) {
		return null;
	}
	
	public boolean hasChildren( Object object ) {
		boolean	result;
		
		try {
			eclox.doxyfile.node.Node	node;
			
			node = (eclox.doxyfile.node.Node) object;
			result = node.getChildren().size() != 0;
		}
		catch( ClassCastException badCast ){
			result = false;
		}
		return result;
	}
	
	/**
	 * Retrieves the relevent childrens of the specified object if it is an instance of the data.Group
	 * interface.
	 * 
	 * @param object	The object from which children must get retrieved.
	 * 
	 * @return	An array of the found children.
	 */
	private Object[] getGroupChildren( Object object ) {
		ArrayList	result = new ArrayList();
		
		if( object instanceof Node ) {
			Node		group = (Node) object;
			Collection	children = group.getChildren();
			Iterator	curChild = children.iterator();
			
			while( curChild.hasNext() ) {
				Object	child = curChild.next();
				
				if( (child instanceof Comment) == false ) {
					 result.add( child );
				}
			}
		}
		
		return result.toArray();
	}
}
