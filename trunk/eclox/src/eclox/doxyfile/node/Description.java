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

/**
 * Implement a doxyfile node description
 * @author gbrocker
 */
public class Description {
	/**
	 * The comment text.
	 */
	private String text;
	
	/**
	 * The comment raw text.
	 */
	private String rawText;
	
	/**
	 * Constructor.
	 * 
	 * @param	rawText		The comment raw text.
	 */
	public Description(String rawText) {
		this.rawText = new String( rawText );		
		this.text = new String( rawText );
		this.text = this.text.replaceAll( "# ", "" );
		this.text = this.text.replaceAll( "#\r\n", "" );
		this.text = this.text.replaceAll( "\r\n", " " );
	}
	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	/**
	 * Retrieves the comment text.
	 * 
	 * @return	A string containing the comment text.
	 */
	public final String getText() {
		return this.text;
	}
	
	/**
	 * Retrieve the comment text representation.
	 * 
	 * @return A string containing the text representation of the node.
	 */
	public String toString() {
		return this.rawText;
	}
}
