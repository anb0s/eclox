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

package eclox.doxyfile.node;

/**
 * @author gbrocker
 */
public class Comment extends Leaf {
	/**
	 * The comment text.
	 */
	private String m_text;
	
	/**
	 * The comment raw text.
	 */
	private String m_rawText;
	
	/**
	 * Constructor.
	 * 
	 * @param	rawText	The comment raw text.
	 */
	public Comment( String rawText ) {
		m_rawText = new String( rawText );		
		m_text = new String( rawText );
		m_text = m_text.replaceAll( "# ", "" );
		m_text = m_text.replaceAll( "#\r\n", "" );
		m_text = m_text.replaceAll( "\r\n", " " );
	}
	
	/**
	 * Make the specified visitor process the comment.
	 * 
	 * @param	visitor	The visitor to feed.
	 */
	public void accept( Visitor visitor ) throws VisitorException {
		visitor.process( this );
	}
	
	/**
	 * Retrieves the comment text.
	 * 
	 * @return	A string containing the comment text.
	 */
	public final String getText() {
		return m_text;
	}
	
	/**
	 * Retrieve the comment text representation.
	 * 
	 * @return A string containing the text representation of the node.
	 */
	public String toString() {
		return m_rawText;
	}
}
