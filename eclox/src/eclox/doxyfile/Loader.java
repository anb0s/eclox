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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import eclox.doxyfile.node.Comment;
import eclox.doxyfile.node.Node;
import eclox.doxyfile.node.Section;
import eclox.doxyfile.node.Tag;

/**
 * Creates doxygen setting.
 * 
 * @author gbrocker
 */
public class Loader {
	
	/**
	 * @brief	Defines the input reader.	
	 */
	private class Reader
	{
		/**
		 * The input stream used for the settings construction.
		 */
		private BufferedReader m_in;
		
		/**
		 * @brief	A line of text that has been previewed..
		 */
		private String m_preview = null;
		
		/**
		 * @brief	Constructor.
		 * 
		 * @param	input	The The input stream to read.
		 */
		public Reader( InputStream input )
		{
			m_in = new java.io.BufferedReader( new java.io.InputStreamReader( input ) );	
		}
		
		/**
		 * @brief	Retrieve the next line of text..
		 * 
		 * @return	A string containing the next line of or null if the end of file
		 * 			has been reached.
		 */
		public String getLine() throws java.io.IOException {
			String	result;
			
			if( m_preview == null ) {
				result = readLine();
			}
			else {
				result = m_preview;
				m_preview = null;
			}
			return result;
		}
		
		/**
		 * @brief	Preview the next line of text. The line of text is read
		 * 			and returned but kept back for the next line retrieval.
		 * 
		 * @return	A string containing the next line of text, or null if the end of
		 * 			file has been reached.
		 */
		public String previewLine() throws java.io.IOException {
			if( m_preview == null ) {
				m_preview = readLine();
			}
			return m_preview;
		}
		
		/**
		 * @brief	Read the next line of text.
		 * 
		 * @return	A string containing the next line of text.
		 * 
		 * @throws java.io.IOException	Error while reading.
		 */
		private String readLine()  throws java.io.IOException {
			String	line = m_in.readLine();
			
			if( line != null )
			{
				line = line.concat("\r\n");
			}
			return line;
		}
	};
	
	/**
	 * @brief	The input object for the loader.
	 */
	private Reader m_in;
	
	/**
	 * Constructor.
	 * 
	 * @param	input	The input stream from which the settings must be constructed.
	 *  
	 * @author gbrocker
	 */
	public Loader( java.io.InputStream input ) {
		m_in = new Reader( input );
	}
	
	/**
	 * Load a doxygen file.
	 * 
	 * @return	The doxygen settings.
	 * 
	 * @author gbrocker
	 */
	public eclox.doxyfile.node.Doxyfile load() throws IOException {
		eclox.doxyfile.node.Doxyfile	doxyfile;
		Node							nextNode;
		eclox.doxyfile.node.Group		curParent;
		
		doxyfile = new eclox.doxyfile.node.Doxyfile(); 
		curParent = doxyfile;
		
		for( nextNode = getNextNode(); nextNode != null; nextNode = getNextNode() ) {
			if( nextNode.getClass() == Section.class ) {
				doxyfile.addChild( nextNode );
			}
			else {
				curParent.addChild( nextNode );
			}
		}
		
		return doxyfile;
	}
	
	/**
	 * @brief	Retrieve the next doxyfeil object.
	 * 
	 * @return	The next doxyfile object.
	 *
	 * @throws java.io.IOException	Error while reading.
	 */
	private Node getNextNode() throws java.io.IOException {
		String	nodeText = null;
		Class	nodeClass = null;

		for(;;)	{
			String line = m_in.getLine();
			
			// We have reached the file end.
			if( line == null )
			{
				break;
			}
			// Or jump empty lines.
			else if( line.equals("\r\n") ) {
				continue;
			}
			
			// We will update the node text and class.
			if( nodeClass == null )	{
				nodeText = line;
				nodeClass = getNodeClass( line );
			}
			else {
				nodeText = nodeText.concat( line );
			}
						
			// We check if the next line will sweet to the current node class.
			String	preview = m_in.previewLine();
			Class	previewClass = getNodeClass( preview );
			
			if( previewClass == Comment.class && nodeClass == Comment.class ) {
				continue;
			}
			else if( previewClass == Comment.class && nodeClass == Section.class ) {
				continue;
			}
			else if( previewClass == Section.class && nodeClass == Section.class ) {
				continue;
			}
			else {
				break;
			}
		}
		return getNodeFromClass( nodeClass, nodeText );
	}
	
	/**
	 * @brief	Retrieve the class of node corresponding to the specified line of text.
	 * 
	 * @return	The class of the node corresponding to the spcified line of text.
	 */
	private static Class getNodeClass( String line ) {	
		String	head = line.substring( 0, 2 );
		Class	nodeClass;
		
		if( head.equals( "#-" ) == true ) {
			nodeClass = Section.class;
		}
		else if( head.equals( "# " ) == true || head.equals( "#\r") == true ) {
			nodeClass = Comment.class;
		}
		else if( head.equals("\r\n") == false ) {
			nodeClass = Tag.class;
		}
		else {
			nodeClass = null;
		}
		return nodeClass;
	}
	
	/**
	 * @brief	Retrieve a node instance from the specified class and text.
	 * 
	 * @param	nodeClass	A class of node to create.
	 * @param	nodeText	The text of the node.
	 */
	private Node getNodeFromClass( Class nodeClass, String nodeText ) {
		Node	result;
		
		if( nodeClass == Section.class ) {
			result = new Section( nodeText );
		}
		else if( nodeClass == Comment.class ) {
			result = new Comment( nodeText );
		}
		else if( nodeClass == Tag.class ) {
			result = new Tag( nodeText );
		}
		else {
			result = null;
		}
		
		return result;
	}
}
