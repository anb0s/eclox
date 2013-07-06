/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
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
