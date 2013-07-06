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
