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

/**
 * Defines the interface for objects that want to receive notification
 * about changes of an editor.
 * 
 * @author Guillaume Brocker
 */
public interface IEditorListener {

    /**
     * Notifies the receiver that the given editor's state changed.
     * 
     * @param editor the editor that changed
     */
    void editorChanged(IEditor editor);

}
