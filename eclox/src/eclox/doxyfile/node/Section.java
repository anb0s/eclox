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

import eclox.doxyfile.node.util.*;

/**
 * Implements a doxygen tag section.
 * 
 * @author gbrocker
 */
public class Section extends Group {
	/**
	 * The name provider for all section instances.
	 */
	private static final SectionNameProvider m_nameProvider = new SectionNameProvider();
	
	/**
	 * The section header.
	 */
	private String m_header;
	
	/**
	 * The section raw header.
	 */
	private String m_rawHeader;
	
	/**
	 * The section name (deducted from the header)
	 */
	private String m_name;
	
	/**
	 * Constructor.
	 * 
	 * @param	header	A string containing the group header.
	 */
	public Section( String header ) {
		
		m_rawHeader = new String( header );
		m_header = new String( header );
		m_header = m_header.replaceAll( "#-+[\r\n]+", "" );
		m_header = m_header.replaceAll( "# |[\r\n]+", "" );
		m_name = m_nameProvider.getName( m_header );	
	}

	/**
	 * Accept the specified visitor.
	 * 
	 * @param	visitor	The visitor to accept.
	 */	
	public void accept( eclox.doxyfile.node.Visitor visitor ) throws VisitorException {
		visitor.process( this );
	}
	
	/**
	 * Retrieve the section header.
	 * 
	 * @return	A string containing the section header.
	 */
	public final String getHeader() {
		return m_header;
	}
	
	/**
	 * Retrieve the section name.
	 * 
	 * @return	A string containing the section name.
	 */
	public final String getName() {
		return m_name;
	}
	
	/**
	 * Convert the node to its string representation.
	 * 
	 * @return	A string containing the node text representation.
	 */
	public String toString() {
		return m_rawHeader;
	}
}
