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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import eclox.build.Builder;
import eclox.ui.BuildLogView;
import eclox.ui.Plugin;


/**
 * Implement the start build action.
 * 
 * @author gbrocker
 */
public class StartBuildAction extends Action {
	/**
	 * The target editor.
	 */
	private IEditorPart editor;
	
	/**
	 * Constructor.
	 */
	public StartBuildAction() {
		super( "Start Build" );
		
		setImageDescriptor( ImageDescriptor.createFromFile( StartBuildAction.class, "icon_small.gif" ) );
		setToolTipText( "Launch Documentation Compilation");
		setEnabled( false );
	}
	
	/**
	 * Set the active editor.
	 * 
	 * @param targetEditor	The new target editor.
	 */
	public void setEditor( IEditorPart targetEditor ) {
		editor = targetEditor;
		setEnabled( editor != null );
	}
	
	/**
	 * Implement the action job.
	 */
	public void run() {
		if( editor != null ) {
			try {
				IFileEditorInput	fileEditorInput;
				
				fileEditorInput = (IFileEditorInput) editor.getEditorInput();
				BuildLogView.show();
				Builder.getDefault().start( fileEditorInput.getFile() );
			}
			catch( Exception exception ) {
				Plugin.getDefault().showError( exception );
			}
		}
	}
}
