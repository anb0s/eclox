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

package eclox.resource.content;

import org.eclipse.ui.views.properties.IPropertySource;

import eclox.resource.content.adapter.SectionPropertySource;
import eclox.resource.content.util.SectionNameProvider;

/**
 * Implements a doxygen tag section.
 * 
 * @author gbrocker
 */
public class Section extends Group {
	/**
	 * The name provider for all section instances.
	 */
	private static final SectionNameProvider nameProvider = new SectionNameProvider();
	
	/**
	 * The section name.
	 */
	private String name = "";
		
	/**
	 * Accept the specified visitor.
	 * 
	 * @param	visitor	The visitor to accept.
	 */	
	public void accept( eclox.resource.content.Visitor visitor ) throws VisitorException {
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
	 * @see eclox.resource.content.Node#setDescription(eclox.doxyfile.node.Description)
	 */
	public void setDescription(Description description) {
		super.setDescription(description);
		this.name = Section.nameProvider.getName(description.toString());
	}
}
