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

/**
 * Implements the abstract doxyfile composed node class.
 * 
 * @author gbrocker
 */
public class Value extends Simple {
	/**
	 * The value content.
	 */
	private String content = new String();
	
	/**
	 * @see eclox.resource.node.Node#accept(eclox.resource.node.Visitor)
	 */
	public void accept(Visitor visitor) {
		visitor.process(this);
	}
	
	/**
	 * Updates the value content from the specified string.
	 * 
	 * @param	content	a string representing the new value content
	 */
	public void fromString(String content) {
		this.content = new String(content);
		this.updateState( true );
	}
	
	/**
	 * Retrieves the value content as a string.
	 * 
	 * @return	a string representing the value content.
	 */
	public String toString() {
		return new String(this.content);
	}
}
