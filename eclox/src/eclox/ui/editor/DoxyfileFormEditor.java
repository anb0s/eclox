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

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import eclox.core.Services;
import eclox.resource.Doxyfile;
import eclox.resource.content.DoxyfileContent;
import eclox.resource.content.Node;
import eclox.resource.content.Section;
import eclox.resource.content.Tag;
import eclox.resource.content.Visitor;
import eclox.resource.content.VisitorException;

/**
 * Implements a doxyfile form based editor.
 * 
 * @author gbrocker
 */
public class DoxyfileFormEditor extends FormEditor {
	/**
	 * Implements a doxyfile content visitor that will create
	 */
	private class PageFactory implements Visitor {
		/**
		 * The editor where the pages will be created.
		 */
		private FormEditor editor;
		
		/**
		 * Constructor.
		 * 
		 * @param	editor	the form editor where the pages will be created.
		 */
		public PageFactory(FormEditor editor) {
			this.editor = editor;
		}
		
		/**
		 * @see eclox.resource.content.Visitor#process(eclox.resource.content.DoxyfileContent)
		 */
		public void process(DoxyfileContent settings) throws VisitorException {
			try {
				addPage(new OverviewFormPage(this.editor));
				
				Iterator it = settings.getChildren().iterator();
				while(it.hasNext() == true) {
					Node node = (Node) it.next();
					
					node.accept(this);
				}
			}
			catch(Throwable throwable) {
				throw new VisitorException("Unable to create a form editor page.", throwable);
			}
		}
		
		/**
		 * @see eclox.resource.content.Visitor#process(eclox.resource.content.Section)
		 */
		public void process(Section section) throws VisitorException {
			try {
				addPage(new SectionFormPage(this.editor, section));
			}
			catch(Throwable throwable) {
				throw new VisitorException("Unable to create a form editor page.", throwable);
			}
		}
		
		/**
		 * @see eclox.resource.content.Visitor#process(eclox.resource.content.Tag)
		 */
		public void process(Tag tag) throws VisitorException {}
	}
	
	/**
	 * The doxyfile content being edited.
	 */
	private DoxyfileContent doxyfileContent;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	protected void addPages() {
		try {
			this.doxyfileContent.accept(new PageFactory(this));
		}
		catch(Throwable throwable) {
			Services.showError(throwable);
		}
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
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.getDoxyfileContent(input);
		super.init(site, input);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Retrieves the doxyfile contents for the specified input.
	 * 
	 * @param	input	the editor input to use.
	 */
	private void getDoxyfileContent(IEditorInput input) throws PartInitException {
		if( input instanceof org.eclipse.ui.IFileEditorInput ) {			
			try{
				// Load the doxyfile.
				org.eclipse.ui.IFileEditorInput	fileInput = (org.eclipse.ui.IFileEditorInput) input;
				
				this.doxyfileContent = Doxyfile.getContent(fileInput.getFile());
				// TODO reactivate
				//this.doxyfileContent.addNodeListener( new SettingsListener() );
				
				// TODO reactivate
				// Set the selection provider.
//				SelectionProvider selectionProvider = new SelectionProvider();
//				
//				site.setSelectionProvider(selectionProvider);
//				//selectionProvider.setSelection(new StructuredSelection(fileInput.getFile()));
//				selectionProvider.setSelection(new StructuredSelection(m_settings));
//				this.setPartName(input.getName());
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
}
