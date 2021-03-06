/*******************************************************************************
 * Copyright (C) 2003-2005, 2013 Guillaume Brocker
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

package eclox.ui.editor.advanced.filters;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.Group;
import eclox.core.doxyfiles.Setting;

/**
 * Implements a filter that shows settings by groups.
 *
 * @author gbrocker
 */
public class ByGroup implements IFilter {

    /**
     * the doxyfile being filtered
     */
    private Doxyfile doxyfile;

    /**
     * the combo control displaying all group that are selectable by the user
     */
    private Combo combo;

    /**
     * the saved combo selection index
     */
    private int savedComboSelection = 0;

    /**
     * the current viewer beging filtered.
     */
    private StructuredViewer viewer;

    /**
     * the current viewer filter that filters objects displayed in the setting viewer
     */
    private MyViewerFilter viewerFilter;

    /**
     * Implements a selection listener for the managed combo control.
     */
    private class MySelectionListener implements SelectionListener {

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            // Pre-condition
            assert viewer != null;

            viewer.refresh();
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            // Pre-condition
            assert viewer != null;

            viewer.refresh();
        }

    }

    /**
     * Implements a structure viewer filter that will filter setting according to the
     * selected group
     */
    private class MyViewerFilter extends ViewerFilter {

        /**
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            // Pre-condition
            assert combo != null;
            assert element instanceof Setting;

            // Retrieves the selected group name.
            int groupIndex = combo.getSelectionIndex();
            String groupName = groupIndex >= 0 ? combo.getItem(groupIndex) : null;

            // Tests if the given element is in the right group.
            if (groupName != null) {
                Setting setting = (Setting) element;
                String settingGroup = setting.getProperty(Setting.GROUP);
                return (settingGroup != null) ? settingGroup.equals(groupName) : false;
            } else {
                return false;
            }
        }

    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#setDoxyfile(eclox.doxyfiles.Doxyfile)
     */
    public void setDoxyfile(Doxyfile doxyfile) {
        this.doxyfile = doxyfile;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#createControls(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    public void createControls(IManagedForm managedForm, Composite parent) {
        // Pre-condition
        assert combo == null;
        assert doxyfile != null;

        // Creates the managed combo control.
        combo = new Combo(parent, SWT.FLAT | SWT.BORDER | SWT.READ_ONLY);
        combo.addSelectionListener(new MySelectionListener());
        parent.setLayout(new FillLayout());

        // Fills the combo with group names.
        Object[] objects = doxyfile.getGroups();
        int i;
        for (i = 0; i < objects.length; ++i) {
            Group group = (Group) objects[i];
            combo.add(group.getName());
        }

        // Restores the combo selection.
        combo.select(savedComboSelection);

        // Post-condition
        assert combo != null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#createViewerFilters(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void createViewerFilters(StructuredViewer viewer) {
        // Pre-condition
        assert this.viewerFilter == null;
        assert this.viewer == null;

        // Creates the viewer filter.
        this.viewerFilter = new MyViewerFilter();
        this.viewer = viewer;
        this.viewer.addFilter(viewerFilter);

        // Post-condition
        assert this.viewerFilter != null;
        assert this.viewer == viewer;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#disposeControls()
     */
    public void disposeControls() {
        // Pre-condition
        assert combo != null;

        // Saves the combo selection.
        savedComboSelection = combo.getSelectionIndex();

        // Diposes the managed combo control.
        combo.getParent().setLayout(null);
        combo.dispose();
        combo = null;

        // Post-condition
        assert combo == null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#disposeViewerFilers(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void disposeViewerFilers(StructuredViewer viewer) {
        // Pre-condition
        assert this.viewerFilter != null;
        assert this.viewer == viewer;

        // Disposes the viewer filter.
        this.viewer.removeFilter(viewerFilter);
        this.viewer = null;
        this.viewerFilter = null;

        // Post-condition
        assert this.viewerFilter == null;
        assert this.viewer == null;
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#getName()
     */
    public String getName() {
        return "By Group";
    }

}
