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

package eclox.ui.editor.form;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

import eclox.ui.editor.form.settings.Block;



/**
 * Implements the settings form page
 * 
 * @author gbrocker
 */
public class SettingsPage extends FormPage {

    /**
     * The page identifier.
     */
    public static final String ID = "setting";
    
    /**
     * The editor.
     */
    private Editor editor;
    
    /**
     * The master/detail block.
     */
    private Block block = new Block();
    
    /**
     * Constructor.
     * 
     * @param	editor	the editor instance to attach to.
     * 
     * @author gbrocker
     */
    public SettingsPage(Editor editor) {
        super(editor, SettingsPage.ID, "All Settings");
        this.editor = editor;
    }
    
    /**
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent(IManagedForm managedForm) {
        managedForm.getForm().setText(this.getTitle());
        this.block.createContent(managedForm);
        this.block.setInput(this.editor.getDoxyfile());
    }
}
