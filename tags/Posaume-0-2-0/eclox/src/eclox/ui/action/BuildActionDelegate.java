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

package eclox.ui.action;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

import eclox.build.Builder;
import eclox.ui.BuildLogView;
import eclox.ui.Plugin;
import eclox.ui.dialog.DoxyfileSelecterDialog;

/**
 * Implement the action handling for the build action.
 * 
 * @author gbrocker
 */
public class BuildActionDelegate implements IWorkbenchWindowPulldownDelegate {
	/**
	 * The doxyfile history.
	 */
	List doxyfileHistory = new LinkedList();
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		// TODO Auto-generated method stub
		return null;
	}

	/** (non-Javadoc)
	 */
	public void dispose() {
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
		try {
			DoxyfileSelecterDialog	doxyfileSelecter = new DoxyfileSelecterDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			IFile					doxyfile = null;
			
			doxyfileSelecter.open();
			doxyfile = doxyfileSelecter.getDoxyfile();
			if(doxyfile != null) {
				BuildLogView.show();
				Builder.getDefault().start(doxyfile);
			}
		}
		
		catch(Throwable throwable) {
			Plugin.getDefault().showError(throwable);
		}
	}

	/**
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
