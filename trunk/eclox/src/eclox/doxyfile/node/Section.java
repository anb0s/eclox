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

import org.eclipse.ui.views.properties.IPropertySource;

import eclox.doxyfile.Doxyfile;
import eclox.doxyfile.adapter.SectionPropertySource;

/**
 * Implements a doxygen tag section.
 * 
 * @author gbrocker
 */
public class Section extends Group {
	/**
	 * The name provider for all section instances.
	 */
//	private static final SectionNameProvider m_nameProvider = new SectionNameProvider();
	
	/**
	 * The section name.
	 */
	private String name = "";
	
	/**
	 * Constructor.
	 * 
	 * @param	doxyfile	The doxyfile the section belongs to.
	 */
	public Section(Doxyfile doxyfile) {
		super(doxyfile);
		
//		m_rawHeader = new String( header );
//		m_header = new String( header );
//		m_header = m_header.replaceAll( "#-+[\r\n]+", "" );
//		m_header = m_header.replaceAll( "# |[\r\n]+", "" );
//		m_name = m_nameProvider.getName( m_header );	
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
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		Object	result = null;
		
		if(adapter.equals(IPropertySource.class) == true) {
			result = new SectionPropertySource(this);
		}
		return result;
	}
	
	/**
	 * Retrieve the section name.
	 * 
	 * @return	A string containing the section name.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set the name of the section.
	 *
	 * @param name	a new name for the section
	 */
	public void setName(String name) {
		this.name = name;
		this.setDirtyInternal();
	}
}
