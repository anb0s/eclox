/*******************************************************************************
 * Copyright (C) 2003-2005, 2013, Guillaume Brocker
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import eclox.core.doxyfiles.Doxyfile;

/**
 * Implements a setting filter that shows all settings.
 * 
 * @author gbrocker
 */
public class All implements IFilter {

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#setDoxyfile(eclox.doxyfiles.Doxyfile)
     */
    public void setDoxyfile(Doxyfile doxyfile) {
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#createControls(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    public void createControls(IManagedForm managedForm, Composite parent) {
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#createViewerFilters(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void createViewerFilters(StructuredViewer viewer) {
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#disposeControls()
     */
    public void disposeControls() {
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#disposeViewerFilers(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void disposeViewerFilers(StructuredViewer viewer) {
    }

    /**
     * @see eclox.ui.editor.advanced.filters.IFilter#getName()
     */
    public String getName() {
        return "All";
    }

}
