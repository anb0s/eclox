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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eclox.core.Services;
import eclox.resource.Doxyfile;
import eclox.resource.content.NodeEvent;
import eclox.resource.content.NodeListener;
import eclox.ui.editor.parts.Hint;
import eclox.ui.editor.parts.Sections;
import eclox.ui.editor.parts.Tags;
import eclox.util.SelectionProvider;

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
	private eclox.resource.content.DoxyfileContent m_settings;
	
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
	 * TODO	Implement !
	 */	
	public void setFocus() {
	}
	
	public void init( org.eclipse.ui.IEditorSite site, org.eclipse.ui.IEditorInput input ) throws org.eclipse.ui.PartInitException {
		setSite( site );
		setInput( input );
		
		if( input instanceof org.eclipse.ui.IFileEditorInput ) {			
			try{
				// Load the doxyfile.
				org.eclipse.ui.IFileEditorInput	fileInput = (org.eclipse.ui.IFileEditorInput) input;
				
				m_settings = Doxyfile.getContent(fileInput.getFile());
				m_settings.addNodeListener( new SettingsListener() );
				
				// Set the selection provider.
				SelectionProvider selectionProvider = new SelectionProvider();
				
				site.setSelectionProvider(selectionProvider);
				//selectionProvider.setSelection(new StructuredSelection(fileInput.getFile()));
				selectionProvider.setSelection(new StructuredSelection(m_settings));
				this.setPartName(input.getName());
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
		
		details = new Hint( tags );
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
	public void doSave(org.eclipse.core.runtime.IProgressMonitor progressMonitor) {
		try {
			Doxyfile.setContent(this.m_settings, ((org.eclipse.ui.IFileEditorInput)getEditorInput()).getFile(), progressMonitor);
			m_settings.setClean();
		}
		catch(Throwable throwable) {
			Services.showError(throwable);
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
}
