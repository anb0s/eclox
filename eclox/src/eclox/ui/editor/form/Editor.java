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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import eclox.doxyfiles.Doxyfile;
import eclox.ui.editor.form.pages.OverviewPage;
import eclox.ui.editor.form.pages.SettingsPage;
import eclox.ui.editor.form.pages.SourcePage;



/**
 * @author gbrocker
 */
public class Editor extends FormEditor {
    
    /**
     * The doxyfile content.
     */
    private Doxyfile doxyfile;

    /**
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
            // TODO reactivate
            //this.addPage(new OverviewPage(this));
            this.addPage(new SettingsPage(this));
            // TODO reactivate
            //this.addPage(new SourcePage(this));
        }
        catch( Throwable throwable ) {}
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }
    
    /**
     * Retrieves the doxyfile attached to the editor.
     * 
     * @return	a doxyfile instance
     */
    public Doxyfile getDoxyfile() {
        return this.doxyfile;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        try {
            IFileEditorInput	fileInput = (IFileEditorInput) input;
            
            this.doxyfile = new Doxyfile(fileInput.getFile());
	        this.setPartName(input.getName());
	        super.init(site, input);
        }
        catch( PartInitException partInitException ) {
            throw partInitException;
        }
        catch( Throwable throwable ) {
            throw new PartInitException( "Unexpected error. "+throwable.getMessage(), throwable );
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }
}
