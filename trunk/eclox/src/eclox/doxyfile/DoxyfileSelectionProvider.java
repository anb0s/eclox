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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import eclox.util.ListenerManager;

/**
 * Implement a selection provider for the view.
 * 
 * @author gbrocker
 */
public class DoxyfileSelectionProvider extends ListenerManager implements ISelectionProvider {
	/**
	 * The current selection.
	 */
	private ISelection selection;
	
	/**
	 * Constructor.
	 */
	public DoxyfileSelectionProvider() {
		super(ISelectionChangedListener.class);
	}
	
	/**
	 * Register a new selection change listener.
	 * 
	 * @param	listener	The listener to register.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		super.addListener(listener);
	}

	/**
	 * Retrieves the current selection.
	 * 
	 * @return	The current selection.
	 */
	public ISelection getSelection() {
		return this.selection;
	}

	/**
	 * Unregister a selection change listener.
	 * 
	 * @param	listener	The listener to unregister.
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		super.removeListener(listener);
	}

	/**
	 * Set the new selection.
	 * 
	 * @param	selection	The new selection.
	 */
	public void setSelection(ISelection selection) {
		this.selection = selection;
		this.fireSelectionChangedEvent();
	}
	
	/**
	 * Fire the selection change event to the listeners.
	 */
	private void fireSelectionChangedEvent() {
		super.fireEvent(new SelectionChangedEvent(this, this.selection), "selectionChanged");
	} 
}