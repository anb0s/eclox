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

package eclox.ui.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

import eclox.build.BuildHistory;
import eclox.build.BuildHistoryEvent;
import eclox.build.BuildHistoryListener;
import eclox.build.BuildJob;
import eclox.doxyfile.Doxyfile;
import eclox.doxyfile.DoxyfileSelection;
import eclox.ui.Plugin;
import eclox.ui.dialog.DoxyfileSelecterDialog;
import eclox.ui.view.BuildLogView;

/**
 * Implement the action handling for the build action.
 * 
 * @author gbrocker
 */
public class BuildActionDelegate implements IWorkbenchWindowPulldownDelegate, BuildHistoryListener {
	/**
	 * Listens for the popup menu items pointing to doxyfiles to build.
	 * 
	 * @author gbrocker
	 */
	public class MenuSelectionListener implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			processData(e.widget.getData());
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			processData(e.widget.getData());
		}
		
		private void processData(Object data) {
			boolean forceChoose = false;
			
			if(data != null && data instanceof IFile) {
				nextDoxyfile = (IFile) data;
				forceChoose = false;
			}
			else {
				forceChoose = true;
			}
			run(forceChoose);
		}
	}
	
	/**
	 * The contextual menu.
	 */
	private Menu menu;
	
	/**
	 * The next doxyfile to build.
	 */
	private IFile nextDoxyfile;
	
	/**
	 * Notify that a build history has changed.
	 * 
	 * @param	event	The buld event object.
	 */
	public void buildHistoryChanged(BuildHistoryEvent event) {
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		disposeMenu();
		if(BuildHistory.getDefault().size() != 0) {
			this.menu = new Menu(parent);
		
			// Fill it up with the build history items.
			IFile[]	doxyfiles = BuildHistory.getDefault().toArray();
			for(int i=0; i < doxyfiles.length; i++) {
				MenuItem menuItem = new MenuItem(this.menu, SWT.PUSH);
			
				menuItem.addSelectionListener(new MenuSelectionListener());
				menuItem.setData(doxyfiles[i]);
				menuItem.setText(doxyfiles[i].getFullPath().toString());				
			}
			
			// Add the fallback menu item to let the user choose another doxyfile.
			MenuItem separatorMenuItem = new MenuItem(this.menu, SWT.SEPARATOR);
			MenuItem chooseMenuItem = new MenuItem(this.menu, SWT.PUSH);
			
			chooseMenuItem.addSelectionListener(new MenuSelectionListener());
			chooseMenuItem.setText("Choose Doxyfile...");			 
		}
		return this.menu;
	}

	/**
	 * Dispose the delegate.
	 */
	public void dispose() {
		disposeMenu();
	}

	/**
	 * Initialization of the action delegate.
	 */
	public void init(IWorkbenchWindow window) {
	}

	/**
	 * This method is called by the proxy action when the action has been triggered.
	 * 
	 * @param	action	the action proxy that handles the presentation portion of the action.
	 */
	public void run(IAction action) {
		this.run(false);
	}

	/**
	 * Notify that the current selection changed.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// Test if the current selection is not empty.
		if(selection != null) {
			if(selection.isEmpty() == true) {
				this.nextDoxyfile = null;
			}
			if(selection instanceof DoxyfileSelection) {
				DoxyfileSelection doxyfileSel = (DoxyfileSelection) selection;
				
				this.nextDoxyfile = doxyfileSel.doxyfile;
			}
			else if(selection instanceof IStructuredSelection) {
				IStructuredSelection	structSel = (IStructuredSelection) selection;
				Object					element = structSel.getFirstElement();

				this.nextDoxyfile = null;
				if(element != null && element instanceof IFile) {
					IFile	fileElement = (IFile) element;

					if(Doxyfile.isFileNameValid(fileElement.getName()) == true) {
						this.nextDoxyfile = fileElement;
					}
				}
			}
		}
		
		// If there is no next doxyfile to build and the history is not empty
		// set the first history element as the next doxyfile.
		if(this.nextDoxyfile == null && BuildHistory.getDefault().size() != 0) {
			IFile[]	historyFiles = BuildHistory.getDefault().toArray();
			this.nextDoxyfile = historyFiles[0];
		}
		
		// Update the action tooltip.
		String	tooltipText = this.nextDoxyfile != null ?
			"Build " + this.nextDoxyfile.getFullPath().toString() :
			"Choose Next Doxyfile";
			
		action.setToolTipText(tooltipText);
	}
	
	/**
	 * Internal run. Use the next doxyfile specified to determine what to do.
	 * 
	 * @param	forceChoose	true to ask the user for a doxyfile to build.
	 */
	protected void run(boolean forceChoose) {
		try {
			IFile	doxyfile = forceChoose == true ? null : this.nextDoxyfile;
			
			// If thereis no next doxyfile to build, ask the user for one.
			if(doxyfile == null) {
				DoxyfileSelecterDialog doxyfileSelecter = new DoxyfileSelecterDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				
				doxyfileSelecter.open();
				doxyfile = doxyfileSelecter.getDoxyfile();
				doxyfileSelecter.dispose();
			}
			
			// If there is a doxyfile, build it.
			if(doxyfile != null) {
				BuildLogView.show();
				BuildJob.getDefault().setDoxyfile(doxyfile);
				BuildJob.getDefault().schedule();
			}
		}
		catch(Throwable throwable) {
			Plugin.getDefault().showError(throwable);
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
}
