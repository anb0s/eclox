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

package eclox.doxyfile;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;

/**
 * @author gbrocker
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DoxyfileSelection implements ISelection {
	/**
	 * The doxyfile being selected, or null if none.
	 */
	public IFile doxyfile;
	
	/**
	 * Constructor
	 * 
	 * @param	doxyfile	The selected doxyfile.
	 */
	public DoxyfileSelection(IFile doxyfile) {
		this.doxyfile = doxyfile;
	}

	/**
	 * Returns whether this selection is empty.
	 * 
	 * @return	true if this selection is empty, and false otherwise 
	 */
	public boolean isEmpty() {
		return doxyfile == null;
	}

}
