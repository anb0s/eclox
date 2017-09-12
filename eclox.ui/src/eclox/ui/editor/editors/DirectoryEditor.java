/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Implements a setting editor that allows to browse for directories 
 * either in the workspace or in the file system.
 * 
 * @author gbrocker
 */
/**
 * @author gbrocker
 *
 */
public class DirectoryEditor extends TextEditor {

    /**
     * the push button for browsing the file system
     */
    private Button browseFileSystemButton;

    /**
     * Implements the selection listener attached to the push buttons.
     */
    class MySelectionListener implements SelectionListener {

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            if (e.widget == browseFileSystemButton) {
                browseFileSystem();
            }
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            if (e.widget == browseFileSystemButton) {
                browseFileSystem();
            }
        }

    }

    public void createContent(Composite parent, FormToolkit formToolkit) {
        super.createContent(parent, formToolkit);

        // Create controls and their associated layout data.
        SelectionListener selectionListener = new MySelectionListener();
        GridLayout layout = (GridLayout) parent.getLayout();

        layout.numColumns = 2;

        browseFileSystemButton = formToolkit.createButton(parent, "Browse...", 0);
        browseFileSystemButton.addSelectionListener(selectionListener);
    }

    /**
     * @see eclox.ui.editor.editors.TextEditor#dispose()
     */
    public void dispose() {
        // Local cleaning.
        browseFileSystemButton.dispose();

        // Default cleaning.
        super.dispose();
    }

    /**
     * Browses the file system for a path and updates the managed text field.
     */
    private void browseFileSystem() {
        String result;
        result = Convenience.browseFileSystemForDirectory(text.getShell(), getInput().getOwner(),
                getInput().getValue());
        if (result != null) {
            super.text.setText(result);
        }
    }

    /**
     * @see eclox.ui.editor.editors.TextEditor#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        assert browseFileSystemButton != null;

        browseFileSystemButton.setEnabled(enabled);
        super.setEnabled(enabled);
    }

}
