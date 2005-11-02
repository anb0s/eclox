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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import eclox.doxyfiles.Doxyfile;

public class Custom implements IFilter {
    
    /**
     * the combo box containing the text used for the setting filtering
     */
    Combo combo;

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#createControls(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    public void createControls(IManagedForm managedForm, Composite parent) {
        // Pre-condition
        assert combo == null;
        
        // Creates the combo control allowing the user to enter text to search
        combo = new Combo( parent, SWT.FLAT|SWT.BORDER );
        parent.setLayout( new FillLayout() );
        
        // Post-condition
        assert combo != null;
    }

    /* (non-Javadoc)
     * @see eclox.ui.editor.form.settings.filters.IFilter#createViewerFilters(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void createViewerFilters(StructuredViewer viewer) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see eclox.ui.editor.form.settings.filters.IFilter#disposeControls()
     */
    public void disposeControls() {
        // Pre-condition
        assert combo != null;
        
        // Disposes the managed combo widget.
        combo.dispose();
        combo = null;
        
        // Post-condition
        assert combo == null;
    }

    /* (non-Javadoc)
     * @see eclox.ui.editor.form.settings.filters.IFilter#disposeViewerFilers(org.eclipse.jface.viewers.StructuredViewer)
     */
    public void disposeViewerFilers(StructuredViewer viewer) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see eclox.ui.editor.form.settings.filters.IFilter#getName()
     */
    public String getName() {
        return "Custom";
    }

    /* (non-Javadoc)
     * @see eclox.ui.editor.form.settings.filters.IFilter#setDoxyfile(eclox.doxyfiles.Doxyfile)
     */
    public void setDoxyfile(Doxyfile doxyfile) {
        // TODO Auto-generated method stub
        
    }
    
    

}
