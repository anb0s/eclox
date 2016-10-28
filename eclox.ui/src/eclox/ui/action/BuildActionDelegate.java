/*******************************************************************************
 * Copyright (C) 2003, 2004, 2007, 2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2016, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Add ability to use Doxyfile not in project scope
 *
 ******************************************************************************/

package eclox.ui.action;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxygen.BuildJob;
import eclox.ui.DoxyfileSelector;
import eclox.ui.Plugin;
import eclox.ui.wizard.NewDoxyfileWizard;

/**
 * Implement the action handling for the build action.
 *
 * @author gbrocker
 */
public class BuildActionDelegate implements IWorkbenchWindowPulldownDelegate {
	/**
	 * Listens for the popup menu items pointing to doxyfiles to build.
	 *
	 * @author Guillaume Brocker
	 */
	private class MenuSelectionListener implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			processData(e.widget.getData());
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			processData(e.widget.getData());
		}

		private void processData(Object data) {
		    Doxyfile doxyfile = null;
			if(data != null) {
			    if (data instanceof Doxyfile) {
			        doxyfile = (Doxyfile)data;
			    } else if (data instanceof IFile) {
			        doxyfile = new Doxyfile((IFile)data, null);
			    } else if (data instanceof File) {
			        doxyfile = new Doxyfile(null, (File)data);
			    }
			}
			if (doxyfile != null) {
			    nextDoxyfile = doxyfile;
			    doRun(false);
			} else {
			    doRun(true);
			}
		}
	}

	private Menu				menu;			///< The managed contextual menu.
	private Doxyfile			nextDoxyfile;	///< Rembers the next doxyfile to build.
	private IWorkbenchWindow	window;			///< Holds the reference to the workbench window where the action takes place.

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		disposeMenu();
		this.menu = new Menu(parent);

		// Fill it up with the build history items.
		BuildJob[]	buildJobs = Plugin.getDefault().getBuildManager().getRecentBuildJobs();
		for( int i = buildJobs.length - 1; i >= 0; i-- ) {
			MenuItem menuItem = new MenuItem(this.menu, SWT.PUSH);
			Doxyfile currentDoxyfile = buildJobs[i].getDoxyfile();
			menuItem.addSelectionListener( new MenuSelectionListener() );
			menuItem.setData( currentDoxyfile );
			menuItem.setText( currentDoxyfile.getName() + " [" + currentDoxyfile.getFullPath() + "]" );
		}
		// Add some sugar in the ui
		if( buildJobs.length > 0 ) {
			new MenuItem(this.menu, SWT.SEPARATOR);
		}

		// Add the fall-back menu item to let the user choose another doxyfile.
		MenuItem chooseMenuItem = new MenuItem(this.menu, SWT.PUSH);

		chooseMenuItem.addSelectionListener(new MenuSelectionListener());
		chooseMenuItem.setText("Choose Doxyfile...");

		// Job's done.
		return this.menu;
	}

	/**
	 * Dispose the delegate.
	 */
	public void dispose() {
		// Frees all resources and references.
		disposeMenu();
		nextDoxyfile = null;
		window = null;
	}


	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}


	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			doRun(false);
		}
		catch( Throwable throwable ) {
			MessageDialog.openError(window.getShell(), "Unexpected Error", throwable.toString());
		}
	}


	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		try {
			// Retrieve the next doxyfile to build from the current selection.
		    IFile nextIFile = getDoxyfileFromSelection(selection);

			// Retrieve the next doxyfile from the current editor.
			if( nextIFile != null ) {
			    nextDoxyfile = new Doxyfile(nextIFile, null);
			} else {
			    nextDoxyfile = getDoxyfileFromActiveEditor(window);
			}

			// If there is no next doxyfile to build and the history is not empty
			// set the first history element as the next doxyfile.
			if( nextDoxyfile == null ) {
				BuildJob[]	buildJobs = Plugin.getDefault().getBuildManager().getRecentBuildJobs();
				int			buildJobsCount = buildJobs.length;
				if( buildJobsCount > 0 ) {
					nextDoxyfile = buildJobs[buildJobsCount - 1].getDoxyfile();
				}
			}

			// Check the existence of the doxyfile.
			if( nextDoxyfile != null && nextDoxyfile.exists() == false ) {
				nextDoxyfile = null;
			}

			// Update the tooltip.
			String	tooltipText = nextDoxyfile != null ?
				"Build " + nextDoxyfile.getFullPath() :
				"Choose Next Doxyfile";

			action.setToolTipText(tooltipText);
		}
		catch(Throwable throwable) {
			MessageDialog.openError(window.getShell(), "Unexpected Error", throwable.toString());
		}
	}


	/**
	 * Uses the next doxyfile specified to determine what to do.
	 *
	 * @param	forceChoose	@c true to ask the user for a doxyfile to build.
	 */
	protected void doRun(boolean forceChoose) {
		try {
			Doxyfile		 doxyfile = (forceChoose == true) ? null : this.nextDoxyfile;
			IFile            doxyIFile = null;
			DoxyfileSelector selector = new DoxyfileSelector(null);

			// If there is no next doxyfile to build, ask the user for one.
			if(doxyfile == null) {
				selector.open();
				doxyIFile = selector.getDoxyfile();
				if (doxyIFile != null) {
				    doxyfile = new Doxyfile(doxyIFile, null);
				}
			}

			// If there is no doxyfile,
			// we will prompt the user to create one and then edit it.
			if (doxyfile == null && selector.hadDoxyfiles() == false) {
                doxyIFile = askUserToCreateDoxyfile();
                if (doxyIFile != null) {
                    doxyfile = new Doxyfile(doxyIFile, null);
                }
			}
			else if (doxyfile != null) {
				Plugin.getDefault().getBuildManager().build( doxyfile );
			}
		}
		catch( Throwable throwable ) {
			MessageDialog.openError(window.getShell(), "Unexpected Error", throwable.toString());
		}
	}


	/**
	 * Dispose the owned menu.
	 */
	private void disposeMenu() {
		if(this.menu != null) {
			this.menu.dispose();
			this.menu = null;
		}
	}


	/**
	 * Retrieves a doxyfile from the active editor.
	 *
	 * @param	window	a reference to a workbench window
	 *
	 * @return	a doxfile retrieved from the active editor input.
	 */
	private static Doxyfile getDoxyfileFromActiveEditor( IWorkbenchWindow window ) {
        IFile ifile = null;
        File   file = null;
		IWorkbenchPage activePage		= window.getActivePage();
		IEditorPart    activeEditorPart	= activePage != null ? window.getActivePage().getActiveEditor() : null;
		if(activeEditorPart != null) {
			IEditorInput input  = activeEditorPart.getEditorInput();
            if (input instanceof IFileEditorInput) {
                ifile = ((IFileEditorInput)input).getFile();
            } else if (input instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) input;
                ifile = (IFile) adaptable.getAdapter(IFile.class);
                if (ifile == null) {
                    if (adaptable instanceof FileStoreEditorInput) {
                        URI fileuri = ((FileStoreEditorInput) adaptable).getURI();
                        file = new File(fileuri.getPath());
                    } else {
                        file = (File) adaptable.getAdapter(File.class);
                    }
                }
            }
            if (ifile != null && !Doxyfile.isDoxyfile(ifile)) {
                ifile = null;
            }
		}
		if (ifile != null || file != null) {
		    return new Doxyfile(ifile, file);
		} else {
		    return null;
		}
	}


	/**
	 * Retrieves a doxyfile from the specified selection.
	 *
	 * @return	a doxyfile retrieved from the specified selection.
	 */
	private static IFile getDoxyfileFromSelection(ISelection selection) {
		IFile doxyfile = null;

		// Test if the current selection is not empty.
		if(selection instanceof IStructuredSelection && selection.isEmpty() == false) {
			IStructuredSelection	structSel = (IStructuredSelection) selection;
			Object					element = structSel.getFirstElement();

			if(element != null && element instanceof IFile) {
				IFile	fileElement = (IFile) element;

				if(fileElement.exists() == true && Doxyfile.isDoxyfile(fileElement) == true) {
					doxyfile = fileElement;
				}
			}
		}


		// Job's done.
		return doxyfile;
	}

	/**
	 * Prompts the user to create a new doxyfile.
	 *
	 * @return	a doxyfile, or null if none.
	 */
	private static IFile askUserToCreateDoxyfile() {
		IFile	doxyfile		= null;
		Shell	shell			= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		boolean	wantDoxyfile	= MessageDialog.openQuestion(shell, "No Doxyfile Found", "No doxyfile has been found in opened projects.\n\nDo you want to create a new doxyfile now ?" );

		if( wantDoxyfile ) {
			NewDoxyfileWizard		wizard = new NewDoxyfileWizard();
			ISelection				selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
			IStructuredSelection	strcSelection = (selection != null && selection instanceof IStructuredSelection) ? (IStructuredSelection) selection : new StructuredSelection();

			wizard.init(PlatformUI.getWorkbench(), strcSelection);


			WizardDialog	wizardDialog = new WizardDialog(shell, wizard);

			wizardDialog.open();
			doxyfile = wizard.getDoxyfile();
		}

		return doxyfile;
	}

}
