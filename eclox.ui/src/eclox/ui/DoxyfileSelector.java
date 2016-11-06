/*******************************************************************************
 * Copyright (C) 2003, 2004, 2007, 2008, 2013, Guillaume Brocker
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

package eclox.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.IWorkbenchAdapter;

import eclox.core.Plugin;
import eclox.core.doxyfiles.ResourceCollector;


/**
 * Allow the user to choose one doxyfile among a list.
 *
 * @author gbrocker
 */
public class DoxyfileSelector {

	/**
	 * Implement the tree content provider for the dialog.
	 */
	private static class MyContentProvider implements ITreeContentProvider {
		/**
		 * The doxyfile collection.
		 */
		private Collection<?> m_input = null;

		/**
		 * Disposes of this content provider. This is called by the viewer when it is disposed.
		 */
		public void dispose() {}

		/**
		 * Notifies this content provider that the given viewer's input has been switched to a different element.
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			m_input = (Collection<?>) newInput;
		}

		/**
		 * Returns the child elements of the given parent element.
		 *
		 * @param	parentElement	the parent element
		 *
		 * @return	an array of child elements
		 */
		public Object[] getChildren(Object parentElement) {
			Collection<IFile>	children = new ArrayList<IFile>();
			Iterator<?>	doxyfileIt = m_input.iterator();

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
			Collection<?>	doxyfiles = (Collection<?>) inputElement;
			Object[]	result = null;

			if(doxyfiles != null) {
				Collection<IProject>	projects = new HashSet<IProject>();
				Iterator<?>	doxyfileIt = doxyfiles.iterator();

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
	 * Implement a label provider for the doxyfile selector tree viewer.
	 */
	private static class MyLabelProvider extends LabelProvider {
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
            // Pre-condition.
            assert element instanceof IResource;

            Image               result = null;
			IResource           resourse = (IResource) element;
            IWorkbenchAdapter   workbenchAdapter = (IWorkbenchAdapter) resourse.getAdapter( IWorkbenchAdapter.class );
			if( workbenchAdapter != null ) {
                result = workbenchAdapter.getImageDescriptor( element ).createImage();
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
            // Pre-condition.
            assert element instanceof IResource;

            String              result = null;
            IResource           resourse = (IResource) element;
            IWorkbenchAdapter   workbenchAdapter = (IWorkbenchAdapter) resourse.getAdapter( IWorkbenchAdapter.class );
            if( workbenchAdapter != null ) {
                result = workbenchAdapter.getLabel( element );
            }
            return result;
		}
	}

	/**
	 * Implement a doxyfile selection validator.
	 */
	private static class MySelectionValidator implements ISelectionStatusValidator {
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
					Plugin.getDefault().getBundle().getSymbolicName(),
					0,
					"",
					null
				);
			}
			else {
				result = new Status(
					Status.ERROR,
					Plugin.getDefault().getBundle().getSymbolicName(),
					0,
					"You must choose a Doxyfile to build.",
					null);
			}
			return result;
		}
	}

	private boolean		hadDoxyfiles = false;	///< Tells if there were doxyfiles for selection.
	private IFile		doxyfile = null;		///< The selected doxyfile
	private IResource	root = null;			///< The root resource that is that will be starting point for doxyfiles search.


	/**
	 * Initializes DoxyfileSelector
	 *
	 * @param	rootResource		the root resource to search for doxyfiles, the workspace root if null
	 */
	public DoxyfileSelector( IResource rootResource ) {
		this.root = rootResource;
	}

	/**
	 * Opens the selection dialog and tells if the user validated to selection.
	 *
	 * @return	true when a selection was made, false otherwise.
	 */
	public boolean open() {
		Shell					shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ISelection				selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		IStructuredSelection	structuredSelection = (selection != null && selection instanceof IStructuredSelection) ? (IStructuredSelection) selection : new StructuredSelection();
		boolean					result = false;

		try	{
			ResourceCollector	collector = root != null ? ResourceCollector.run(root) : ResourceCollector.run();

			hadDoxyfiles = collector.isEmpty() == false;

			if( collector.isEmpty() == false ) {
				ElementTreeSelectionDialog	selectionDialog = new ElementTreeSelectionDialog(shell, new MyLabelProvider(), new MyContentProvider());

				selectionDialog.setAllowMultiple( false );
				selectionDialog.setInput( collector.getDoxyfiles() );
				selectionDialog.setValidator( new MySelectionValidator() );
		        selectionDialog.setEmptyListMessage( "No Doxyfile found in opened projects." );
		        selectionDialog.setMessage( "Select a Doxyfile:" );
		        selectionDialog.setTitle( "Doxyfile Selection" );
				selectionDialog.setInitialSelections( structuredSelection.toArray() );

				// Opens the selection dialog and save the selection.
				if( selectionDialog.open() == ElementTreeSelectionDialog.OK ) {
					doxyfile = (IFile) selectionDialog.getFirstResult();
					result = true;
				}
			}
		}
		catch( Throwable t ) {
			eclox.ui.Plugin.log(t);
		}

		return result;
	}

	/**
	 * Convenient method that prompts the user to select a doxyfile.
	 *
	 * @param	root	The root resource to search for doxyfiles
	 *
	 * @return	the selected doxyfile, null otherwise
	 */
	public static IFile open( IResource root ) {
		DoxyfileSelector	selector = new DoxyfileSelector(root);

		selector.open();
		return selector.getDoxyfile();
	}

	/**
	 * @brief	Asks the user for a doxyfile.
	 *
	 * @return	a doxyfile, null if none has been choosen
	 */
	public IFile getDoxyfile() {
		return doxyfile;
	}

	/**
	 * @brief	Tells if there were doxyfiles for last selection.
	 *
	 * @return	true or false
	 */
	public boolean hadDoxyfiles() {
		return hadDoxyfiles;
	}
}
