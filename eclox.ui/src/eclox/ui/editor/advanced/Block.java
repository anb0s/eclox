/*
/*******************************************************************************
 * Copyright (C) 2003-2004, 2013, Guillaume Brocker
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
    Block(Doxyfile doxyfile) {
        this.doxyfile = doxyfile;
    }

    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart(IManagedForm managedForm, Composite parent) {
        managedForm.addPart(new MasterPart(parent, managedForm.getToolkit(), doxyfile));
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
    protected void createToolBarActions(IManagedForm managedForm) {
    }

}
