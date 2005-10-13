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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Implements a filter that shows settings by groups.
 * 
 * @author gbrocker
 */
public class ByGroup implements IFilter {
    
    /**
     * the combo control displaying all group that are selectable by the user
     */
    private CCombo combo;
    
    /**
     * the current viewer filter that filters objects displayed in the setting viewer
     */
    private MyViewerFilter viewerFilter;
    
    /**
     * Implements a structure viewer filter that will filter setting according to the
     * selected group
     */
    private class MyViewerFilter extends ViewerFilter {

        /**
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            // TODO Auto-generated method stub to implement.
            return false;
        }
        
    }

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#createControls(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    public void createControls( IManagedForm managedForm, Composite parent ) {
        // Pre-condition
        assert combo == null;
        
        // Creates the managed combo control.
        combo = new CCombo( parent, SWT.FLAT|SWT.BORDER );
        parent.setLayout( new FillLayout() );
        
        // Post-condition
        assert combo != null;
    }

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#createViewerFilters(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void createViewerFilters(StructuredViewer viewer) {
        // Pre-condition
        assert viewerFilter == null;
        
        // Creates the viewer filter.
        viewerFilter = new MyViewerFilter();
        viewer.addFilter( viewerFilter );
        
        // Post-condition
        assert viewerFilter != null;
    }

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#disposeControls()
     */
    public void disposeControls() {
        // Pre-condition
        assert combo != null;
        
        // Diposes the managed combo control.
        combo.getParent().setLayout( null );
        combo.dispose();
        combo = null;
        
        // Post-condition
        assert combo == null;
    }

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#disposeViewerFilers(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void disposeViewerFilers(StructuredViewer viewer) {
        // Pre-condition
        assert viewerFilter != null;
        
        // Disposes the viewer filter.
        viewer.removeFilter( viewerFilter );
        viewerFilter = null;
        
        // Post-condition
        assert viewerFilter == null;
    }

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#getName()
     */
    public String getName() {
        return "By Group";
    }

}
