/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2006 Guillaume Brocker

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

package eclox.ui.editor.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import eclox.ui.editor.Editor;

/**
 * Implements a resource change listener that will trigger relevant actions
 * on the given editor when its input gets updated.
 *  
 * @author Guillaume Brocker
 */
public class ResourceChangeListener implements IResourceChangeListener {

	/**
	 * a reference on the editor to manage
	 */
	private Editor editor;
	
	/**
	 * Constructor
	 * 
	 * @param	editor	the editor to manage
	 */
	public ResourceChangeListener( Editor editor )
	{
		this.editor = editor;
	}
	
	public void resourceChanged(IResourceChangeEvent event)
	{
		IEditorInput		editorInput = editor.getEditorInput();
		IFileEditorInput	fileEditorInput = (IFileEditorInput) editorInput;
		IFile				editorFile = fileEditorInput.getFile();
		IResourceDelta		doxyfileDelta = event.getDelta().findMember( editorFile.getFullPath() );
		
		if( doxyfileDelta != null && doxyfileDelta.getKind() == IResourceDelta.REMOVED )
		{
			closeEditor();
		}
	}
	
	/**
	 * Closes the editor.
	 */
	private void closeEditor()
	{
		editor.getSite().getShell().getDisplay().asyncExec(
					new Runnable() {
						
						public void run() {
							editor.getSite().getPage().closeEditor( editor, false );		
						}
					}
				);
	}

}
