// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2006 Guillaume Brocker
//
// This file is part of eclox.
//
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
//
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

package eclox.ui.editor.form.settings.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import eclox.doxyfiles.Doxyfile;

/**
 * Implements convenience methods for setting editors.
 * 
 * @author gbrocker
 */
public final class Convenience {

	/**
	 * Browses the file system for a directory and retrieves a path in the file system that
	 * may be relative to the given doxyfile's path.
	 * 
	 * @param	shell		a shell that will be the parent of dialogs
	 * @param	doxyfile		a doxyfile that is the reference for relative paths
	 * @param	path			a string containing an initial path
	 * 
	 * 
	 * @return	a string containing the browsed path, or null if none
	 */
	public static String browseFileSystemForDirectory( Shell shell, Doxyfile doxyfile, String path )	{
		// Retrieves the initial path.
		IPath	initialPath = new Path( path );
		if( initialPath.isAbsolute() == false ) {
			initialPath = doxyfile.getParentContainer().getLocation().append( initialPath );
		}
		
		// Displayes the directory dialog to the user
		DirectoryDialog	dialog = new DirectoryDialog( shell );
		String			choosenPathString;
		dialog.setMessage( "Please, select a folder.");
		dialog.setText( "Folder Selection" );
		dialog.setFilterPath( initialPath.toOSString() );
		choosenPathString = dialog.open();
		
		// Parses the result.
		if( choosenPathString != null ) {
			IPath	choosenPath = Path.fromOSString( choosenPathString );
			IPath	finalPath = doxyfile.makePathRelative( choosenPath );
			
			return finalPath.toOSString();
		}
		else {
			return null;
		}
	}

	/**
	 * Browses the workspace for a directory and retrieves a path in the workspace that
	 * may be relative to the given doxyfile's path.
	 * 
	 * @param	shell		a shell that will be the parent of dialogs
	 * @param	doxyfile		a doxyfile that is the reference for relative paths
	 * @param	path			a string containing an initial path
	 * 
	 * 
	 * @return	a string containing the browsed path, or null if none
	 */
	public static String browseWorkspaceForDirectory( Shell shell, Doxyfile doxyfile, String path )	{
		// Retrieves the workspace root container.
		IWorkspaceRoot	root = ResourcesPlugin.getWorkspace().getRoot();
		
		// Prepares the initial root from the current setting value.
		IContainer		initialRoot = null;
		IPath			initialPath = new Path( path );
		if( initialPath.isAbsolute() == true ) {
			initialRoot = root.getContainerForLocation( initialPath );
		}
		else {
			initialRoot = root.getContainerForLocation( doxyfile.getParentContainer().getLocation().append( initialPath ) );
		}

		// If the initial root has not bee retrieved, use the workspace root as default.
		if( initialRoot == null ) {
			initialRoot = root;
		}
		
		// Displayes the selection dialog to the user.
		Object[]					result;
		ContainerSelectionDialog	dialog = new ContainerSelectionDialog( shell, initialRoot, false, "Please, select a folder." );
		dialog.open();
		result = dialog.getResult();
		
		// Parses the result.
		if( result != null && result.length >= 1 ) {
			Path	 		choosenPath = (Path) result[0];
			IContainer	container = (IContainer) root.findMember( choosenPath );
			IPath		containerPath = container.getLocation();
			IPath		finalPath = doxyfile.makePathRelative( containerPath );
			
			return finalPath.toOSString();
		}
		else {
			return null;
		}
	}

}
