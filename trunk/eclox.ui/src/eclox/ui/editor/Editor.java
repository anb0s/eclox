/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
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

package eclox.ui.editor;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
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
/**
 * @author willy
 *
 */
public class Editor extends FormEditor implements ISettingValueListener, IPersistableEditor {
    
	public final static String PROP_SETTING_DIRTY = "dirty";				///< the name of the property attached to a dirty setting.
	public final static String SAVED_ACTIVE_PAGE_ID = "SavedActivePageId";	///< Identifies the memo entry containing the identifier if the saved active page identifier.
	
    private Doxyfile				doxyfile;				///< The doxyfile content.
    private ResourceChangeListener	resourceChangeListener;	///< the resource listener that will manage the editor life-cycle
    private boolean					dirty = false;			///< The dirty state of the editor
    private IMemento				savedState;				///< References a saved state to restore, null otherwise.

    /**
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
            addPage(new eclox.ui.editor.basic.Page(this));
            addPage(new eclox.ui.editor.advanced.Page(this));
            // TODO reactivate
            //this.addPage(new SourcePage(this));
            
            // Restores the saved active page.
            String savedPageId = (savedState != null) ? savedState.getString(SAVED_ACTIVE_PAGE_ID) : null;
            setActivePage(savedPageId);
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
			// Commits all pending changes.
			commitPages(true);
    		
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

    /**
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

    	// Assigns a dynamic property to the setting.
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
        
        // Detaches the resource change listener
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

	/**
	 * @see org.eclipse.ui.IPersistableEditor#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento) {
		savedState = memento;
	}

	/**
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		memento.putString(SAVED_ACTIVE_PAGE_ID, getActivePageInstance().getId());
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		try {
	        IFileEditorInput	fileInput = (IFileEditorInput) input;
	        
	        // Attaches the resource change listener
	        resourceChangeListener = new ResourceChangeListener(this);
	        ResourcesPlugin.getWorkspace().addResourceChangeListener( resourceChangeListener );
	        
	        // Parses the doxyfile and attaches to all settings.
	        this.doxyfile = new Doxyfile( fileInput.getFile() );
	        Iterator	i = this.doxyfile.settingIterator();
	        while( i.hasNext() == true ) {
	            Setting	setting = (Setting) i.next();
	            setting.addSettingListener( this );
	        }
	        
	        // Continue initialization.
	        setPartName( input.getName() );
		}
		catch( Throwable throwable ) {
			MessageDialog.openError(getSite().getShell(), "Unexpected Error", throwable.toString());
		}
	}
    
}
