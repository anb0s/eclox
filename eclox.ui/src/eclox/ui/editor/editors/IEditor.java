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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Defines the interface of doxyfile editors.
 * 
 * @author gbrocker
 */
public interface IEditor {

    /**
     * Adds the given listener to the collectgion of listener who will be notified
     * when the editor gets changed.
     * 
     * @param listener	the listener to register
     * 
     * @see removeListener
     */
    void addListener(IEditorListener listener);

    /**
     * Commits any changes made in the editor.
     */
    public void commit();

    /**
     * Creates the editor contents.
     * 
     * @param	parent		the composite control that will contain the editor controls
     * @param	formToolkit	the form toolkit to use for the control creation
     */
    void createContent(Composite parent, FormToolkit formToolkit);

    /**
     * Asks the editor if it wants to grow vertically to grad all available space.
     * 
     * @return	a boolean telling if the editor wants to grab the available vertical space
     */
    boolean grabVerticalSpace();

    /**
     * Asks the editor to dispose its controls.
     */
    void dispose();

    /**
     * Tells if the editor is dirty.
     * 
     * @return	true or false
     */
    public boolean isDirty();

    /**
     * Tells if the editor is stale, after the underlying setting has changed.
     * 
     * @return	true or false
     */
    public boolean isStale();

    /**
     * Refreshes the editor if is stale. The editor is expected to updates
     * its state using the data of the underlying model.
     */
    public void refresh();

    /**
     * Removes the given listener from the collection of registered listeners.
     * 
     * @param	listener	the listener to un-register
     * 
     * @see		addListener
     */
    public void removeListener(IEditorListener listener);

    /**
     * Enables or disables the receiver.
     * 
     * @param enabled	the new enabled state
     */
    void setEnabled(boolean enabled);

    /**
     * Makes the editor installs the focus on the right widget.
     */
    void setFocus();

}
