/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2004 Guillaume Brocker

	This file is part of eclox.

	eclox is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	any later version.

	eclox is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with eclox; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eclox.doxyfile.DoxyfileSelectionProvider;
import eclox.doxyfile.Loader;
import eclox.doxyfile.Saver;
import eclox.doxyfile.node.NodeEvent;
import eclox.doxyfile.node.NodeListener;
import eclox.ui.editor.parts.Hint;
import eclox.ui.editor.parts.Sections;
import eclox.ui.editor.parts.Tags;

/**
 * This is the editor part implementation.
 * 
 * @author gbrocker
 */
public class Editor extends org.eclipse.ui.part.EditorPart {
	/**
	 * Implement a value listener responsible for the dirty flag change.
	 */
	private class SettingsListener implements NodeListener {
		/**
		 * Process a node clean change notification.
		 * 
		 * @param	event	The event to process.
		 */
		public void nodeClean( NodeEvent event ) {
			firePropertyChange( PROP_DIRTY );			
		}
		
		/**
		 * Process a node dirty change notification.
		 * 
		 * @param	event	The event to process.
		 */
		public void nodeDirty( NodeEvent event ) {
			firePropertyChange( PROP_DIRTY );
		}
	}

	/**
	 * The Doxygen settings to edit.
	 */
	private eclox.doxyfile.Doxyfile m_settings;
	
	/*
	 * IAdapter interface implementation.
	 */
	
	public Object getAdapter( Class adapter ) {
		Object	object = null;
		
		// Get the outliner for the editor.
		if( org.eclipse.ui.views.contentoutline.IContentOutlinePage.class.equals( adapter ) ) {
			object = new eclox.ui.outline.Outliner( m_settings );
		}
		return object;
	}
		
	/**
	 * @todo	Implement !
	 */	
	public void gotoMarker( org.eclipse.core.resources.IMarker marker) {
	}
	
	/**
	 * @todo	Implement !
	 */	
	public void setFocus() {
	}
	
	public void init( org.eclipse.ui.IEditorSite site, org.eclipse.ui.IEditorInput input ) throws org.eclipse.ui.PartInitException {
		setSite( site );
		setInput( input );
		
		if( input instanceof org.eclipse.ui.IFileEditorInput ) {			
			try{
				// Load the doxyfile.
				eclox.doxyfile.Loader			loader;
				org.eclipse.ui.IFileEditorInput	fileInput;
				
				fileInput = (org.eclipse.ui.IFileEditorInput) input; 
				loader = new Loader( fileInput.getFile().getContents() );
				m_settings = loader.getDoxyfile();
				m_settings.addNodeListener( new SettingsListener() );
				
				// Set the selection provider.
				DoxyfileSelectionProvider selectionProvider = new DoxyfileSelectionProvider();
				
				site.setSelectionProvider(selectionProvider);
				selectionProvider.setDoxyfile(fileInput.getFile());
			}
			catch( Throwable throwable ) {
				throw new org.eclipse.ui.PartInitException( "Unable to load doxyfile. " + throwable.getMessage(), throwable );
			}
		}
		else {
			String	message = new String();
			
			message += input.getName();
			message += " is not a valid doxygen configuration !";
			throw new org.eclipse.ui.PartInitException( message );
		} 
	}
	
	/**
	 * Create the control for the editor.
	 * 
	 * @param	parent	The parent composite control for the editor controls.
	 */
	public void createPartControl( org.eclipse.swt.widgets.Composite parent ) {
		org.eclipse.swt.widgets.Control		curControl;
		org.eclipse.swt.layout.GridData		layoutData;
		org.eclipse.swt.layout.GridLayout	layout;
		
		// Create the layout.		
		
		layout = new org.eclipse.swt.layout.GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 1;
		parent.setLayout( layout );
		
		// Create the section management part.
		Sections	sections;
		 
		sections = new Sections();
		curControl = sections.createControl( parent );
		layoutData = new GridData( GridData.FILL_VERTICAL );
		layoutData.widthHint = 200;
		curControl.setLayoutData( layoutData );
		// Don't forget to initialize the section manager content. 
		sections.populate( m_settings );
		
		// Create a second container for the right part since the grid layout has some bugs.
		Composite	container;
		
		container = new Composite( parent, 0 );
		layoutData = new GridData( GridData.FILL_BOTH );
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 1;
		container.setLayout( layout );
		container.setLayoutData( layoutData );
		
		// Create the tag management part.
		Tags	tags;
		
		tags = new Tags( sections );
		curControl = tags.createControl( container );
		layoutData = new GridData( GridData.FILL_BOTH );
		curControl.setLayoutData( layoutData );
		
		// Create the detail part.
		Hint details;
		
		details = new Hint( sections, tags );
		curControl = details.createControl( container );
		layoutData = new GridData( GridData.FILL_HORIZONTAL );
		layoutData.heightHint = 75;
		curControl.setLayoutData( layoutData );
	}
	
	/*
	 * ISaveablePart implementation.
	 */
	 
	/**
	 * Perform the data save.
	 * 
	 * @param	progressMonitor	The monitor to use to report te save progress.
	 */
	public void doSave( org.eclipse.core.runtime.IProgressMonitor progressMonitor ) {
		try {
			Saver					saver;
			IFile					file;
			
			// Get the file content.
			saver = new Saver();
			m_settings.accept( saver );
			// Save the file content.
			file = ((org.eclipse.ui.IFileEditorInput)getEditorInput()).getFile();
			file.setContents( saver, true, true, progressMonitor );
			// Set the nodes as clean.
			m_settings.setClean();
		}
		catch( Exception exceptoin ) {
		}
	}
	
	public void doSaveAs() {
	}
	
	/**
	 * Tell if the editor is dirty.
	 * 
	 * @return	true if a value in the editor has changed, false otheriwse.
	 */
	public boolean isDirty() {
		return m_settings.isDirty();
	}
	
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public boolean isSaveOnCloseNeeded() {
		return false;
	}
	
	
	/*
	 * IWorkbenchPart interface implementation.
	 */
	
	
	public String getTitle() {
		String	title = new String();
		
		if( getEditorInput() != null )	{
			title = getEditorInput().getName();
		}			
		return title;
	}
	
	public org.eclipse.swt.graphics.Image getTitleImage() {
		org.eclipse.swt.graphics.Image	image = null;
		
		if( getEditorInput()!= null ) {
			image = getEditorInput().getImageDescriptor().createImage();		
		}
		return image;
	}
	
	public String getTitleToolTip() {
		String	titleToolTip = new String();
		
		if( getEditorInput() != null ) {
			titleToolTip = getEditorInput().getToolTipText();
		}
		return titleToolTip;
	}
}
