/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2004 Guillaume Brocker
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

package eclox.ui.editor.advanced;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.Setting;

/**
 * @author gbrocker
 */
public class Block extends MasterDetailsBlock {
    
    /**
     * the doxyfile to edit
     */
    private Doxyfile doxyfile;
    
    /**
     * Constructor
     * 
     * @param	doxyfile	the doxyfile to edit
     */
    Block( Doxyfile doxyfile ) {
    	this.doxyfile = doxyfile;
    }
        
    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart(IManagedForm managedForm, Composite parent) {
        managedForm.addPart( new MasterPart(parent, managedForm.getToolkit(), doxyfile) );
    }

    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages(DetailsPart detailsPart) {
        detailsPart.registerPage(Setting.class, new DetailsPage());
    }

    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions(IManagedForm managedForm) {}

}
