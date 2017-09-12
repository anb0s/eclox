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

package eclox.ui.editor.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Implements a multi editor that presents choices in a combo box.
 * 
 * @author Guillaume Brocker
 */
public class ComboMultiEditor extends MultiEditor {

    /**
     * Implements a selection listener that will handle selection changes in the combo control.
     */
    private class MySelectionListener implements SelectionListener {

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            selectState(combo.getItem(combo.getSelectionIndex()));
            commit();
        }

    }

    /**
     * the combo control that is the representation of the editor
     */
    Combo combo;

    /**
     * Constructor
     * 
     * @param	states	an array of string representing all posible states
     */
    ComboMultiEditor(String[] states) {
        super(states);
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    public void createContent(Composite parent, FormToolkit formToolkit) {
        // Pre-condition
        assert combo != null;

        // Creates the combo control.
        combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.addSelectionListener(new MySelectionListener());
        formToolkit.adapt(combo, true, true);

        // Fills the combo with the state names.
        for (int i = 0; i != states.length; ++i) {
            combo.add(states[i].getName());
        }
        combo.setVisibleItemCount(states.length);

        // Installs a layout in the parent composite
        parent.setLayout(new FillLayout());
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#dispose()
     */
    public void dispose() {
        combo = null;
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#grabVerticalSpace()
     */
    public boolean grabVerticalSpace() {
        return false;
    }

    /**
     * @see eclox.ui.editor.basic.MultiEditor#refresh()
     */
    public void refresh() {
        // Pre-condition
        assert combo != null;

        super.refresh();

        // Selectes the string corresponding to the current selection
        State selection = getSelectionAsState();
        if (selection != null) {
            combo.select(combo.indexOf(selection.getName()));
        } else {
            combo.deselectAll();
        }
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        // Pre-condition
        assert combo != null;

        combo.setEnabled(enabled);
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#setFocus()
     */
    public void setFocus() {
        // Pre-condition
        assert combo != null;

        combo.setFocus();
    }

}
