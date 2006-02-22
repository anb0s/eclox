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

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import eclox.core.Services;
import eclox.doxyfiles.Doxyfile;
import eclox.doxyfiles.ISettingValueListener;
import eclox.doxyfiles.Setting;
import eclox.doxyfiles.io.Serializer;



/**
 * @author gbrocker
 */
public class Editor extends FormEditor implements ISettingValueListener {
    
	/**
	 * the name of the property attached to a dirty setting
	 */
	public final static String PROP_SETTING_DIRTY = "dirty";
	
    /**
     * The doxyfile content.
     */
    private Doxyfile doxyfile;
    
    /**
     * The dirty state of the editor
     */
    private boolean dirty = false;

    /**
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
            // TODO reactivate
            //this.addPage(new OverviewPage(this));
            this.addPage( new SettingsPage(this) );
            // TODO reactivate
            //this.addPage(new SourcePage(this));
        }
        catch( Throwable throwable ) {}
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor ) {
        // Retrieves the file input.
    		IEditorInput			editorInput = this.getEditorInput();
    		IFileEditorInput		fileEditorInput = (IFileEditorInput) editorInput;
    		IFile				file = fileEditorInput.getFile();
    	
    		try {
    			// Comits all pending changes.
	    		getActivePageInstance().getManagedForm().commit( true );
	    		
	        	// Stores the doxyfile content.
		    	Serializer	serializer = new Serializer( doxyfile );
		    	file.setContents( serializer, false, true, monitor );
		    	
		    	// Clears the dirty property set on some settings.
		    	Iterator		i = doxyfile.settingIterator();
		    	while( i.hasNext() ) {
		    		Setting	setting = (Setting) i.next();
		    		setting.removeProperty( PROP_SETTING_DIRTY );
		    	}
		    	
		    	// Resets the dirty flag.
		    	this.dirty = false;
		    	this.firePropertyChange( IEditorPart.PROP_DIRTY );
	    	}
	    	catch( Throwable throwable ) {
	    		Services.showError( throwable );
	    	}
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
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException {
        try {
            IFileEditorInput	fileInput = (IFileEditorInput) input;
            
            // Parses the doxyfile and attaches to all settings.
            this.doxyfile = new Doxyfile(fileInput.getFile());
            Iterator	i = this.doxyfile.settingIterator();
            while( i.hasNext() == true ) {
                Setting	setting = (Setting) i.next();
                setting.addSettingListener( this );
            }
            
            // Continue initialization.
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
    
    /**
     * @see eclox.doxyfiles.ISettingListener#settingValueChanged(eclox.doxyfiles.Setting)
     */
    public void settingValueChanged( Setting setting ) {
    		// Updates the internal editor state.
        this.dirty = true;
        this.firePropertyChange( IEditorPart.PROP_DIRTY );
        
        // Assignes a dynamic property to the setting.
        setting.setProperty( PROP_SETTING_DIRTY, "yes" );
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        // Unregisters the editor from the settings
        Iterator		i = this.doxyfile.settingIterator();
        while( i.hasNext() == true ) {
            Setting	setting = (Setting) i.next();
            setting.removeSettingListener( this );
        }
        
        // Un-references the doxyfile.
        this.doxyfile = null;

        // Continue...
        super.dispose();
    }
    
    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    public boolean isDirty() {
        return super.isDirty() || this.dirty;
    }
}
