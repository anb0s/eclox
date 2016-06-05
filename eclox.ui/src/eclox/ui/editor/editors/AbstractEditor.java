/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/

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
	private Collection<IEditorListener> listeners = new HashSet<IEditorListener>();

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
		Iterator<IEditorListener>	i = listeners.iterator();
		while( i.hasNext() ) {
			IEditorListener	listener = (IEditorListener) i.next();

			listener.editorChanged(this);
		}
	}

}
