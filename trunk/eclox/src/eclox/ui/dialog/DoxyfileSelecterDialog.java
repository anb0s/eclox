/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003 Guillaume Brocker

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

package eclox.ui.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import eclox.ui.Plugin;


/**
 * Allow the user to choose one doxyfile among a list.
 * 
 * @author gbrocker
 */
public class DoxyfileSelecterDialog {
	/**
	 * A doxyfile resource collector.
	 * 
	 * It scans the default workspace for doxyfiles.
	 */
	private class DoxyfileCollector implements IResourceProxyVisitor {
		/**
		 * The collection of found doxyfiles.
		 */
		private Collection m_doxyfiles = new Vector();
		
		/**
		 * Constructor.
		 */
		public DoxyfileCollector() throws CoreException {
			ResourcesPlugin.getWorkspace().getRoot().accept(this, 0);						
		}
		
		/**
		 * Retrieve the found doxyfiles.
		 * 
		 * @return	A collection containing IFile items.
		 */
		public Collection getDoxyfiles() {
			return m_doxyfiles;
		}
		
		/**
		 * Make the resource visite.
		 * 
		 * @param	proxy	A resource proxy to process.
		 */
		public boolean visit(IResourceProxy proxy) throws CoreException {
			boolean	result = false;
			
			switch(proxy.getType()) {
				case IResource.ROOT:
				case IResource.PROJECT:
				case IResource.FOLDER:
					result = true;
					break;
					
				case IResource.FILE:
					Pattern pattern = Pattern.compile(".*doxyfile.*", Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(proxy.getName());
					if(matcher.matches() == true) {
						m_doxyfiles.add(proxy.requestResource());
					}
					result = false;
					break;
			}
			return result;
		}
	} 

	/**
	 * Implement the tree contant provider for the dialog.
	 */
	private class DoxyfileContentProvider implements ITreeContentProvider {
		/**
		 * The doxyfile collection.
		 */
		private Collection m_input = null;
		
		/**
		 * Disposes of this content provider. This is called by the viewer when it is disposed.
		 */
		public void dispose() {}
		
		/**
		 * Notifies this content provider that the given viewer's input has been switched to a different element.
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			m_input = (Collection) newInput;
		}
		
		/**
		 * Returns the child elements of the given parent element.
		 *  
		 * @param	parentElement	the parent element 
		 *
		 * @return	an array of child elements
		 */
		public Object[] getChildren(Object parentElement) {
			Collection	children = new ArrayList();
			Iterator	doxyfileIt = m_input.iterator();
			
			while(doxyfileIt.hasNext()) {
				IFile	doxyfile = (IFile) doxyfileIt.next();
				
				if(doxyfile.getProject() == parentElement) {
					children.add(doxyfile);
				}				
			}			
			return children.toArray();
		}
		
		/**
		 * Returns the elements to display in the viewer when its input is set to the given element. These elements can be presented as rows in a table, items in a list, etc. The result is not modified by the viewer.
		 *  
		 * @param	inputElement	the input element
		 *  
		 * @return	the array of elements to display in the viewer
		 */
		public Object[] getElements(Object inputElement) {
			Collection	doxyfiles = (Collection) inputElement;
			Object[]	result = null;
			
			if(doxyfiles != null) {
				Collection	projects = new HashSet();
				Iterator	doxyfileIt = doxyfiles.iterator();
				
				while(doxyfileIt.hasNext() == true) {
					IFile	doxyfile = (IFile) doxyfileIt.next();
					
					projects.add(doxyfile.getProject());
				}			
				result = projects.toArray();
			}
			else {
				result = new Object[0];
			}
			return result;
		}
		
		/**
		 * Returns the parent for the given element, or null indicating that the parent can't be computed.
		 * 
		 * In this case the tree-structured viewer can't expand a given node correctly if requested.
		 * 
		 * @param	element	the element 
		 * 
		 * @return	the parent element, or null if it has none or if the parent cannot be computed.
		 */
		public Object getParent(Object element) {
			Object	result = null;
		
			if(element instanceof IProject) {
				return null;
			}
			else if(element instanceof IFile) {
				return ((IFile)element).getProject();
			}
			return result;
		}	
  
		/** 
		 * Returns whether the given element has children.
		 * 
		 * Intended as an optimization for when the viewer does not need the actual children. Clients may be able to implement this more efficiently than getChildren.
		 * 
		 * @param	element	the element
		 * 
		 * @return	true if the given element has children, and false if it has no children
		 */
		public boolean hasChildren(Object element) {
			return element instanceof IProject;
		}
	}
	
	/**
	 * Implement a label provider for the doxyfile selector tree vierwer. 
	 */
	private class DoxyfileLabelProvider extends LabelProvider {
		/**
		 * Returns the image for the label of the given element.
		 * The image is owned by the label provider and must not be disposed directly.
		 * Instead, dispose the label provider when no longer needed.
		 * 
		 * @param	element	the element for which to provide the label image 
		 * 
		 * @return	the image used to label the element,
		 * 			or null if there is no image for the given object
		 */
		public Image getImage(Object element) {
			Image	result= null;
			String	imageName = null;
			
			if(element instanceof IProject) {
				imageName = ISharedImages.IMG_OBJ_PROJECT;
			}
			else if(element instanceof IFile) {
				imageName = ISharedImages.IMG_OBJ_FILE;
			}
			
			if(imageName != null) {
				result = PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
			}
			return result;
		}		
		
		/**
		 * The LabelProvider implementation of this ILabelProvider method returns
		 * the element's toString string. Subclasses may override.
		 * 
		 * @param	element	the element for which to provide the label text
		 * 
		 * @return	the text string used to label the element,
		 * 			or null if there is no text label for the given object 
		 */
		public String getText(Object element) {
			String	result = null;
			
			if(element instanceof IResource) {
				result = ((IResource)element).getName();				
			}
			return result;
		}
	}
	
	/**
	 * Implement a doxyfiel selection validator.
	 */
	private class DoxyfileSelectionValidator implements ISelectionStatusValidator {
		/**
		 * Validates an array of elements and returns the resulting status.
		 * 
		 * @param	selection	The elements to validate
		 * 
		 * @return	The resulting status	
		 */
		public IStatus validate(Object[] selection) {
			IStatus	result = null;
			
			if(selection.length == 1 && selection[0] instanceof IFile) {
				result = new Status(
					Status.OK,
					Plugin.getDefault().getDescriptor().getUniqueIdentifier(),
					0,
					"",
					null
				);
			}
			else {
				result = new Status(
					Status.ERROR,
					Plugin.getDefault().getDescriptor().getUniqueIdentifier(),
					0,
					"You must choose a Doxyfile to build.",
					null);
			}
			return result;							
		}		
	}
	
	/**
	 * The selection dialog.
	 */
	ElementTreeSelectionDialog selectionDialog;

	/**
	 * Constructor.
	 *
	 * @param	parentShell	The parent shell, or null to create a top-level shell.
	 */
	public DoxyfileSelecterDialog(Shell parentShell) {
		this.selectionDialog = new ElementTreeSelectionDialog(
			parentShell,
			new DoxyfileLabelProvider(),
			new DoxyfileContentProvider()
		);
	}
	
	/**
	 * Retrieve the selected doxyfile.
	 * 
	 * @return	The selecter doxyfile, or null if none.
	 */
	public IFile getDoxyfile() {
		return (IFile) this.selectionDialog.getFirstResult();
	}
	
	/**
	 * Open the selection dialog.
	 * 
	 * @return	the dialog return value.
	 * 
	 * @throws CoreException
	 * 
	 * TODO	Complete comments.
	 */
	public int open() throws CoreException {
		DoxyfileCollector	collector = new DoxyfileCollector();
		
		this.selectionDialog.setAllowMultiple(false);
		this.selectionDialog.setInput(collector.getDoxyfiles());
		this.selectionDialog.setValidator(new DoxyfileSelectionValidator());

		// Query the current selection.
		ISelection	selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if(selection != null && selection instanceof IStructuredSelection) {
			this.selectionDialog.setInitialSelections(
				((IStructuredSelection)selection).toArray());
		}
		
		return this.selectionDialog.open();
	}
}
