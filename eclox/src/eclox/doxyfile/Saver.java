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

package eclox.doxyfile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

import eclox.doxyfile.node.Comment;
import eclox.doxyfile.node.Group;
import eclox.doxyfile.node.Node;
import eclox.doxyfile.node.Doxyfile;
import eclox.doxyfile.node.Section;
import eclox.doxyfile.node.Tag;
import eclox.doxyfile.node.Visitor;
import eclox.doxyfile.node.VisitorException;

/**
 * Implements the default doxygen data saver. It stores the settings in plain text, as close as possible
 * to the original doxygen file structure.
 * 
 * @author gbrocker
 */
public class Saver extends InputStream implements Visitor {
	/**
	 * The list of all nodes to process.
	 */
	List m_nodes = new LinkedList();
	
	/**
	 * The current stream buffer.
	 */
	String m_buffer = new String();
	
	/**
	 * Make the visitor process the specified commment.
	 * 
	 * @param	comment	The comment to process.
	 */
	public void process( Comment comment ) throws VisitorException {
		m_nodes.add( comment );		
	}
	
	/**
	 * Make the visitor process the specified root.
	 * 
	 * @param	root	The root node to process.
	 */
	public void process( Doxyfile root ) throws VisitorException {
		processChildren( root );
	}
	
	/**
	 * Make the visitor process the specified section.
	 * 
	 * @param	section	The section to process.
	 */
	public void process( Section section ) throws VisitorException {
		m_nodes.add( section );
		processChildren( section );
	}
	
	/**
	 * Make the visitor process the specified tag.
	 * 
	 * @param	tag	The tag to process.
	 */
	public void process( Tag tag ) throws VisitorException {
		m_nodes.add( tag );
	}
	
	/**
	 * Read and return a byte from the save stream.
	 * 
	 * @return	The next byte read, or -1 if none.
	 */
	public int read() throws IOException {
		int	result;
		
		// Check the stream buffer state.
		if( m_buffer.length() == 0 && m_nodes.isEmpty() == false ){
			Node	node = (Node) m_nodes.remove( 0 );
	
			m_buffer = node.toString() + "\r\n\r\n";
		}
		
		// Get the next stream char.
		if( m_buffer.length() != 0 ) {
			result = m_buffer.charAt( 0 );
			m_buffer = m_buffer.substring( 1 );
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
	private void processChildren( Group group ) throws VisitorException {
		Iterator	childPointer = group.getChildren().iterator();
		
		while( childPointer.hasNext() ) {
			Node	child = (Node)childPointer.next();
			
			child.accept( this );
		}
	}
}
