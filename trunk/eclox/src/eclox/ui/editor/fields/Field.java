/*
	eclox
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

package eclox.ui.editor.fields;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;

/**
 * The abstract field class.
 * 
 * @author gbrocker
 */
public interface Field {
	/**
	 * Tell the field to attach to the specified tag. This method is called only once, just after
	 * the field creation.
	 * 
	 * @param	tag	The tag to attach to.
	 */
	public void attachTo( eclox.doxyfile.node.Tag tag );
	
	/**
	 * Detach the field from the value.
	 */
	public void detach();
	
	/**
	 * Create the control of the field.
	 * 
	 * @param	parent	The composite object instance that must be the parent of the field control.
	 * 
	 * @return	The control of the field.
	 */
	public Control createControl( Composite parent );
}
