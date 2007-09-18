// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2007 Guillaume Brocker
//
// This file is part of eclox.
//
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
//
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

package eclox.ui.editor.editors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Implements the IEditor for all listener management and notification
 * aspects
 * 
 * @author Guillaume Brocker
 */
public abstract class AbstractEditor implements IEditor {
	
	/**
	 * the registery of listeners
	 */
	private Collection listeners = new HashSet();

	/**
	 * @see eclox.ui.editor.editors.IEditor#addListener(eclox.ui.editor.editors.IEditorListener)
	 */
	public void addListener(IEditorListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#removeListener(eclox.ui.editor.editors.IEditorListener)
	 */
	public void removeListener(IEditorListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notifies registered listeners that the dirty state of the editor changed.
	 */
	protected void fireEditorChanged() {
		Iterator	i = listeners.iterator();
		while( i.hasNext() ) {
			IEditorListener	listener = (IEditorListener) i.next();
			
			listener.editorChanged(this);
		}
	}

}
