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

package eclox.ui.editor.parts;

import eclox.util.ListenerManager;

/**
 * Implement the generic manager class.
 * 
 * @author gbrocker
 */
public abstract class Selection extends ListenerManager {
	/**
	 * Constructor.
	 */
	public Selection() {
		super( SelectionListener.class );
	}
	
	/**
	 * Add the specified listener.
	 * 
	 * @param	listener	The listener to add.
	 */
	public void addSelectionListener( SelectionListener listener ) {
		super.addListener( listener );
	}
	
	/**
	 * Remove the specified listener.
	 * 
	 * @param	listener	The listener to remove.
	 */
	public void removeSelectionListener( SelectionListener listener ) {
		super.removeListener( listener );
	}
	
	/**
	 * Retrieve the selected object in the manager.
	 * 
	 * @return	The selected object in the manager. Can be null if no object is selected.
	 */
	public abstract Object getSelected();
	
	/**
	 * Notify all listeners that the value has changed.
	 * 
	 * @todo	Handle exceptions.
	 */
	protected void notifySelectionChanged() {
		super.fireEvent( new SelectionEvent( this ), "selectionChanged" );	 	
	}
}
