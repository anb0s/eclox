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
import java.util.Collection;

/**
 * Implement a leaf node class.
 * 
 * @author gbrocker
 */
public abstract class Leaf extends Node {
	/**
	 * Retrieve the leaf children, i.e. no children.
	 *
	 * @return	The leaf children. 
	 */
	public Collection getChildren() {
		return new ArrayList();
	}
	
	/**
	 * Set the node clean.
	 */
	public void setClean() {
		if( isDirty() ) {
			super.setCleanInternal();
		}
	}
}
