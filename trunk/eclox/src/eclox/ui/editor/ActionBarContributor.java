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

package eclox.ui.editor;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Implement the editor action bar contributor.
 * 
 * @author gbrocker
 */
public class ActionBarContributor extends EditorActionBarContributor {
	/**
	 * The start build action.
	 */
	private StartBuildAction	startBuildAction = new StartBuildAction();
	
	/**
	 * Contribute to the toolbar.
	 * 
	 * @param	toolBarManager	The toolbar manager to contribute to.
	 */
	public void contributeToToolBar( IToolBarManager toolBarManager ) {
		toolBarManager.add( startBuildAction );
	}
	
	/**
	 * Set the new active editor.
	 */
	public void setActiveEditor( IEditorPart targetEditor ) {
		startBuildAction.setEditor( targetEditor );
	} 
}
