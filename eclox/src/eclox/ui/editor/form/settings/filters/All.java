/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2005 Guillaume Brocker
 * 
 * This file is part of eclox.
 * 
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 * 
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA    
 */

package eclox.ui.editor.form.settings.filters;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Implements a setting filter that shows all settings.
 * 
 * @author gbrocker
 */
public class All implements IFilter {

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#createControls(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    public void createControls(IManagedForm managedForm, Composite parent) {}

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#createViewerFilters(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void createViewerFilters(StructuredViewer viewer) {}

   /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#disposeControls()
     */
    public void disposeControls() {}

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#disposeViewerFilers(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void disposeViewerFilers(StructuredViewer viewer) {}

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#getName()
     */
    public String getName() {
        return "All";
    }

}
