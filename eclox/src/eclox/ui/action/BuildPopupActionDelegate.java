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
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import eclox.core.Services;

/**
 * Implement a popup menu action delegate for contextual building.
 * 
 * @author gbrocker
 */
public class BuildPopupActionDelegate implements IObjectActionDelegate {
	/**
	 * The current selected item.
	 */
	private ISelection selection;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

	public void run(IAction action) {
		if(this.selection != null && this.selection instanceof IStructuredSelection) {
			try {
				IStructuredSelection	structuredSelection = (IStructuredSelection) this.selection;
				IFile					doxyfile = (IFile) structuredSelection.getFirstElement();
				
				Services.buildDoxyfile( doxyfile );
			}
			catch(Throwable throwable) {
				Services.showError(throwable);
			}				
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
