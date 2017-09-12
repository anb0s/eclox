/*******************************************************************************
 * Copyright (C) 2003-2005, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - images handling
 *
 ******************************************************************************/

package eclox.ui.editor.advanced.filters;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.Setting;
import eclox.ui.Images;
import eclox.ui.Plugin;

public class Custom implements IFilter {

    /**
     * the combo box containing the text used for the setting filtering
     */
    private Combo combo;

    /**
     * the clear button
     */
    private Button clearButton;

    /**
     * an array of strings containing the saved combo items
     */
    private String[] savedComboItems = null;

    /**
     * a string containing the saved combo text
     */
    private String savedComboText = new String();

    /**
     * a string containing the text to use for filtering
     */
    private String filterText;

    /**
     * the viewer being filtered
     */
    private StructuredViewer viewer;

    /**
     * the viewer filter currently installed in the viewer to filter
     */
    private MyViewerFiler viewerFilter;

    /**
     * Implements a timer task that will trigger the viewer refresh after
     * the user changed the custom filter text and update the items available
     * in the filter combo control.
     */
    private class MyRunnable implements Runnable {

        private String referenceText;

        public MyRunnable(String referenceText) {
            this.referenceText = new String(referenceText);
        }

        public void run() {
            // Pre-condition
            assert combo != null;
            assert viewer != null;

            // Retrieves the combo text and checks that the user has not entered additionnal text.
            String comboText = combo.getText();
            if (comboText.equalsIgnoreCase(referenceText) == false) {
                return;
            }

            if (comboText.length() == 0) {
                combo.select(-1);
            } else {
                // Scans combo text items for the combo text.
                String comboItems[] = combo.getItems();
                boolean found = false;
                for (int i = 0; i < comboItems.length; ++i) {
                    if (comboItems[i].equalsIgnoreCase(comboText) == true) {
                        found = true;
                        break;
                    }
                }
                if (found == false) {
                    combo.add(comboText, 0);
                }
            }

            // Refreshes the combo so the elements will be refiltered.
            viewer.refresh();
        }
    };

    /**
     * Implements a viewer filter that will search for setting matching the
     * string entered by the user in the combo control.
     */
    private class MyViewerFiler extends ViewerFilter {

        /**
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            // Pre-condition
            assert element instanceof Setting;

            // Tests if the current setting matches the query string.
            if (filterText != null) {
                Setting setting = (Setting) element;
                final String settingText = setting.getProperty(Setting.TEXT);

                return (settingText != null) && (settingText.toLowerCase().indexOf(filterText.toLowerCase()) != -1);
            } else {
                return true;
            }
        }

    };

    /**
     * Implements a combo modification listener that will trigger
     * the viewer refresh.
     */
    private class MyComboModifyListener implements ModifyListener {

        public void modifyText(ModifyEvent e) {
            // Pre-condition
            assert combo != null;

            filterText = new String(combo.getText());
            combo.getDisplay().timerExec(450, new MyRunnable(filterText));
        }

    };

    /**
     * Implements a selection listsner for the managed clear button.
     */
    private class MyClearSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
            // Pre-condition
            assert combo != null;

            // Clears the managed combo text.
            combo.select(-1);
            combo.setText(new String());
        }

        public void widgetSelected(SelectionEvent e) {
            // Pre-condition
            assert combo != null;

            // Clears the managed combo text.
            combo.select(-1);
            combo.setText(new String());
        }

    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#createControls(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    public void createControls(IManagedForm managedForm, Composite parent) {
        // Pre-condition
        assert combo == null;
        assert clearButton == null;

        // Creates the combo control allowing the user to enter text to search.
        combo = new Combo(parent, SWT.FLAT | SWT.BORDER);
        // Fills the combo with the eventual saved items.
        if (this.savedComboItems != null) {
            combo.setItems(this.savedComboItems);
        } else {
            combo.setText("type filter text");
        }
        // Restores the saved combo selected item.
        combo.setText(this.savedComboText);
        combo.setSelection(new Point(0, combo.getText().length()));
        // Attaches a modify listener.
        combo.addModifyListener(new MyComboModifyListener());

        // Creates the clear button.
        clearButton = managedForm.getToolkit().createButton(parent, null, SWT.FLAT);
        clearButton.setImage(Plugin.getImage(Images.ERASE.getId()));
        clearButton.addSelectionListener(new MyClearSelectionListener());

        // Installs the layout into the parent, and layout data on controls
        GridLayout layout = new GridLayout(2, false);
        layout.marginTop = 0;
        layout.marginRight = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);
        combo.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL));

        // Post-condition
        assert combo != null;
        assert clearButton != null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#createViewerFilters(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void createViewerFilters(StructuredViewer viewer) {
        // Pre-condition
        assert viewer == null;
        assert viewerFilter == null;

        // Installes the viewer filter.
        this.viewer = viewer;
        this.viewerFilter = new MyViewerFiler();
        this.viewer.addFilter(this.viewerFilter);

        // Post-condition
        assert this.viewer != null;
        assert this.viewerFilter != null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#disposeControls()
     */
    public void disposeControls() {
        // Pre-condition
        assert combo != null;
        assert clearButton != null;

        // Saves some state of the managed combo widget.
        this.savedComboItems = combo.getItems();
        this.savedComboText = combo.getText();

        // Disposes all managed widgets.
        combo.dispose();
        clearButton.dispose();
        combo = null;
        clearButton = null;

        // Post-condition
        assert combo == null;
        assert clearButton == null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#disposeViewerFilers(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void disposeViewerFilers(StructuredViewer viewer) {
        // Pre-condition
        assert this.viewer != null;
        assert viewerFilter != null;

        // Uninstalles the viewer filter.
        viewer.removeFilter(this.viewerFilter);
        this.viewerFilter = null;
        this.viewer = null;

        // Post-condition
        assert this.viewer == null;
        assert this.viewerFilter == null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#getName()
     */
    public String getName() {
        return "Custom";
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#setDoxyfile(eclox.doxyfiles.Doxyfile)
     */
    public void setDoxyfile(Doxyfile doxyfile) {
        // Nothing to do.
    }
}
