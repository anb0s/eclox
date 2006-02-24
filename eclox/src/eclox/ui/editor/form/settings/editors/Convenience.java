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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
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
	 * flag that activates the facility to browse for a workspace file
	 */
	public static final int BROWSE_WORSPACE_FILE = 1;
	
	/**
	 * flag that activates the facility to browse for a workspace directory
	 */
	public static final int BROWSE_WORSPACE_DIRECTORY = 2;
	
	/**
	 * flag that activates the facility to browse for a file system file
	 */
	public static final int BROWSE_FILESYSTEM_FILE = 4;
	
	/**
	 * flag that activates the facility to browse for a file system directory
	 */
	public static final int BROWSE_FILESYSTEM_DIRECTORY = 8;
	
	/**
	 * flag that activates the facility to browse for a workspace or file system file
	 */
	public static final int BROWSE_FILE = BROWSE_WORSPACE_FILE|BROWSE_FILESYSTEM_FILE;
	
	/**
	 * flag that activates the facility to browse for a workspace or file system directory
	 */
	public static final int BROWSE_DIRECTORY = BROWSE_WORSPACE_DIRECTORY|BROWSE_FILESYSTEM_DIRECTORY;
	
	/**
	 * flag that activates the facility to browse for a workspace file or directory
	 */
	public static final int BROWSE_WORSPACE_PATH = BROWSE_WORSPACE_FILE|BROWSE_WORSPACE_DIRECTORY;
	
	/**
	 * flag that activates the facility to browse for a file system file or directory
	 */
	public static final int BROWSE_FILESYSTEM_PATH = BROWSE_FILESYSTEM_FILE|BROWSE_FILESYSTEM_DIRECTORY;
	
	/**
	 * flag that activates the facility to browse for a workspace or file system path
	 */
	public static final int BROWSE_ALL = BROWSE_WORSPACE_PATH|BROWSE_FILESYSTEM_PATH;
	
	
	/**
	 * @brief	Retrieves a path from the user that will be relative to the given doxyfile.
	 * 
	 * @param	shell		the parent shell
	 * @param	doxyfile	the reference doxyfile
	 * @param	initPath	the initial path (may relative to the given doxyfile or absolute)
	 * @param	style		a combination of flags to tell which browse facilities will be available
	 * 
	 * @return	a string containing the path entered by the user or null if none
	 */
	public static String browserForPath( Shell shell, Doxyfile doxyfile, String initPath, int style ) {
		PathInputDialog	dialog =  new PathInputDialog( shell, doxyfile, initPath, style );
		
		if( dialog.open() == PathInputDialog.OK ) {
			return dialog.getValue();
		}
		else {
			return null;
		}
	}

	/**
	 * Browses the file system for a directory and retrieves a path in the file system that
	 * may be relative to the given doxyfile's path.
	 * 
	 * @param	shell		a shell that will be the parent of dialogs
	 * @param	doxyfile	a doxyfile that is the reference for relative paths
	 * @param	path		a string containing an initial path
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
	

	/**
	 * Implements a specialized input dialog that adds buttons for browsing
	 * workspace or file system for paths. 
	 */
	private static class PathInputDialog extends InputDialog {

		private final static int BROWSE_WORKSPACE_FILE_ID = IDialogConstants.CLIENT_ID + 1;
		
		private final static int BROWSE_WORKSPACE_DIRECTORY_ID = IDialogConstants.CLIENT_ID + 2;
		
		private final static int BROWSE_FILESYSTEM_FILE_ID = IDialogConstants.CLIENT_ID + 3;
		
		private final static int BROWSE_FILESYSTEM_DIRECTORY_ID = IDialogConstants.CLIENT_ID + 4;
		
		private Doxyfile doxyfile;
		
		private int styles;
		
		/**
		 * Constructor
		 * 
		 * @param	shell		the parent shell
		 * @param	doxyfile	the reference doxyfile
		 * @param	initValue	the initial value
	 	 * @param	styles		a combination of browse facility flags
	 	 */
		public PathInputDialog( Shell shell, Doxyfile doxyfile, String initValue, int styles ) {
			super(
					shell,
					"Value Edition",
					"Edit the path. You may use buttons bellow to browse for a path.",
					initValue,
					null );
			
			this.doxyfile = doxyfile;
			this.styles = styles;
		}
		
		protected void createButtonsForButtonBar(Composite parent) {
			// Do default button creations
			super.createButtonsForButtonBar(parent);
			
			// Creates additionnal buttons
			if( (styles & BROWSE_WORSPACE_FILE) != 0 ) {
				createButton( parent, BROWSE_WORKSPACE_FILE_ID, "Workspace File...", false );
			}
			if( (styles & BROWSE_WORSPACE_DIRECTORY) != 0 ) {
				createButton( parent, BROWSE_WORKSPACE_DIRECTORY_ID, "Workspace Directory...", false );
			}
			if( (styles & BROWSE_FILESYSTEM_FILE) != 0 ) {
				createButton( parent, BROWSE_FILESYSTEM_FILE_ID, "File System File...", false );
			}
			if( (styles & BROWSE_FILESYSTEM_DIRECTORY) != 0 ) {
				createButton( parent, BROWSE_FILESYSTEM_DIRECTORY_ID, "File System Directory...", false );
			}
		}

		protected void buttonPressed(int buttonId) {
			// Retrieves the path if one of the browse buttons is pressed.
			String	path = null;
			switch( buttonId ) {
				case BROWSE_FILESYSTEM_DIRECTORY_ID:
					path = Convenience.browseFileSystemForDirectory( getShell(), doxyfile, getText().getText() );
					break;
				
				case BROWSE_WORKSPACE_DIRECTORY_ID:
					path = Convenience.browseWorkspaceForDirectory( getShell(), doxyfile, getText().getText() );
					break;
					
				case BROWSE_FILESYSTEM_FILE_ID:
				case BROWSE_WORKSPACE_FILE_ID:
				default:
					super.buttonPressed(buttonId);
			}
			
			// Updates the input text if a path has been retrieved.
			if( path != null ) {
				getText().setText( path );
			}
		}		
	}

}
