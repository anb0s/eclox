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

package eclox.resource.content.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import eclox.resource.content.Description;
import eclox.resource.content.DoxyfileContent;
import eclox.resource.content.Group;
import eclox.resource.content.Node;
import eclox.resource.content.Section;
import eclox.resource.content.Tag;
import eclox.resource.content.Visitor;
import eclox.resource.content.VisitorException;

/**
 * Implements the default doxygen data saver. It stores the settings in plain text, as close as possible
 * to the original doxygen file structure.
 * 
 * @author gbrocker
 */
public class Serializer extends InputStream implements Visitor {
	/**
	 * The current stream buffer.
	 */
	private String buffer = new String();
	
	/**
	 * Make the visitor process the specified root.
	 * 
	 * @param	root	The root node to process.
	 */
	public void process(DoxyfileContent root) throws VisitorException {
		Description	version = root.getVersion();
		this.buffer += version != null ? version.toString() + "\r\n" : "";
		
		Description description = root.getDescription();
		this.buffer += description != null ? description.toString() + "\r\n" : "";
		
		processChildren(root);
	}
	
	/**
	 * Make the visitor process the specified section.
	 * 
	 * @param	section	The section to process.
	 */
	public void process(Section section) throws VisitorException {
		this.buffer += section.getDescription().toString();
		this.buffer += "\r\n";
		processChildren(section);
	}
	
	/**
	 * Make the visitor process the specified tag.
	 * 
	 * @param	tag	The tag to process.
	 */
	public void process( Tag tag ) throws VisitorException {
		Description description = tag.getDescription();
		this.buffer += description != null ? description.toString() + "\r\n" : "";
		
		this.buffer += tag.getName();
		this.buffer += " = ";
		this.buffer += tag.getValue().toString();
		this.buffer += "\r\n\r\n";
	}
	
	/**
	 * Read and return a byte from the save stream.
	 * 
	 * @return	The next byte read, or -1 if none.
	 */
	public int read() throws IOException {
		int	result;
			
		// Get the next stream char.
		if( this.buffer.length() != 0 ) {
			result = this.buffer.charAt( 0 );
			this.buffer = this.buffer.substring( 1 );
		}
		else {
			result = -1;
		}		
		return result;
	}
	
	/**
	 * Helper that process the children of the specified node.
	 * 
	 * @param node	The node for which the child must be processed.
	 * 
	 * @throws VisitorException
	 */
	private void processChildren(Group group) throws VisitorException {
		Iterator	childPointer = group.getChildren().iterator();
		
		while(childPointer.hasNext() == true) {
			Node	child = (Node)childPointer.next();
			
			child.accept( this );
		}
	}
}
