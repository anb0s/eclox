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


package eclox.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import eclox.build.Builder;

/**
 * Handles all the contextual popup menu for doxygen configuration files.
 * 
 * @author gbrocker
 */
public class PopupMenuDelegate implements IObjectActionDelegate {
	/**
	 * The selected file.
	 */
	IFile m_file = null;
	
	/**
	 * The part that is active for the popup menu delegate.
	 */
	private IWorkbenchPart m_activePart = null;
	
	public void run( IAction action ) {
		IWorkbenchPage	page = m_activePart.getSite().getPage(); 
		
		try {
			Builder	builder = Builder.getDefault();
			
			BuildLogView.show( page );
			builder.start( m_file );
		}
		catch( Throwable throwable ) {
			Plugin.getDefault().showError( throwable );
		}
		
	}
	
	/**
	 * Notify that the selection has changed.
	 */
	public void selectionChanged( IAction action, ISelection selection ) {
		if( selection != null ) {
			IStructuredSelection	structuredSelection = (IStructuredSelection) selection;
			
			m_file = (IFile) structuredSelection.getFirstElement();
		}
		else {
			m_file = null;
		}		
	}
	
	/**
	 * Set the active part for the delegate.
	 */
	public void setActivePart( IAction action, IWorkbenchPart part ) {
		m_activePart = part;
	}
}
