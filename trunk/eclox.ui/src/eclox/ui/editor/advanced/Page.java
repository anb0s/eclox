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

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import eclox.ui.editor.Editor;



/**
 * Implements the settings form page
 * 
 * @author gbrocker
 */
public class Page extends FormPage {

    /**
     * The page identifier.
     */
    public static final String ID = "advanced";
    
    /**
     * The master/detail block.
     */
    private Block block;
    
    /**
     * Constructor.
     * 
     * @param	editor	the editor instance to attach to.
     * 
     * @author gbrocker
     */
    public Page(Editor editor) {
        super(editor, Page.ID, "Advanced");
        block = new Block( editor.getDoxyfile() );
    }
    
    /**
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent(IManagedForm managedForm) {
        managedForm.getForm().setText(this.getTitle());
        this.block.createContent(managedForm);
    }
}
