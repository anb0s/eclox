/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.ui.editor;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.ISettingValueListener;
import eclox.core.doxyfiles.Setting;
import eclox.core.doxyfiles.io.Serializer;
import eclox.ui.editor.internal.ResourceChangeListener;



/**
 * Implements the doxyfile editor.
 * 
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
     * the resource listener that will manage the editor life-cycle
     */
    private ResourceChangeListener resourceChangeListener;
    
    /**
     * The dirty state of the editor
     */
    private boolean dirty = false;

    /**
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
            addPage(new eclox.ui.editor.basic.Page(this));
            addPage(new eclox.ui.editor.advanced.Page(this));
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
		IEditorInput		editorInput = this.getEditorInput();
		IFileEditorInput	fileEditorInput = (IFileEditorInput) editorInput;
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
    		MessageDialog.openError(getSite().getShell(), "Unexpected Error", throwable.toString());
    	}
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs() {
        // TODO implement "save as"
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
            
            // Attaches the resource change listener
            resourceChangeListener = new ResourceChangeListener(this);
            ResourcesPlugin.getWorkspace().addResourceChangeListener( resourceChangeListener );
            
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
        // TODO implement "save as"
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
        
        // Dettaches the resource change listener
        ResourcesPlugin.getWorkspace().removeResourceChangeListener( resourceChangeListener );
        resourceChangeListener = null;

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
