/**
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



/**
 * Implements the doxygen settings root node.
 * 
 * @author gbrocker
 */
public class DoxyfileContent extends Group {
	/**
	 * The doxyfile version.
	 */
	private Description version;
	
	/**
	 * Implement the visitor acceptence.
	 * 
	 * @param	visitor	The visitor to accept.
	 */
	public void accept(Visitor visitor) throws VisitorException {
		visitor.process( this );
	}
	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	/**
	 * Retrieve the doxyfile version.
	 * 
	 * @return	the doxyfile version.
	 */
	public Description getVersion() {
		return this.version;
	}
	
	/**
	 * Set the doxyfile version.
	 * 
	 * @param	version	a doxyfile version.
	 */
	public void setVersion(Description version) {
		this.version = version;
		this.setDirtyInternal();
	}
}
