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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eclox.doxyfiles.Setting;

/**
 * Implements a list editor that handle value compounds as direcoty paths.
 * 
 * @author gbrocker
 */
public class DirectoryListEditor extends ListEditor {

	/**
	 * Implements a specialized input dialog that adds buttons for browsing
	 * workspace or file system for paths. 
	 */
	private class MyInputDialog extends InputDialog {

		private final static int BROWSE_WORKSPACE_ID = IDialogConstants.CLIENT_ID + 1;
		
		private final static int BROWSE_FILESYSTEM_ID = IDialogConstants.CLIENT_ID + 2;
		
		private Setting setting;
		
		/**
		 * Constructor
		 * 
		 * @param	shell		the parent shell
		 * @param	setting		the reference setting
		 * @param	initValue	the initial value
		 */
		MyInputDialog( Shell shell, Setting setting, String initValue ) {
			super(
					shell,
					"Value Edition",
					"Edit the path. You may use the buttons below to browse the workspace or the file system.",
					initValue,
					null );
			
			this.setting = setting;
		}
		
		protected void createButtonsForButtonBar(Composite parent) {
			// Do default button creations
			super.createButtonsForButtonBar(parent);
			
			// Creates additionnal buttons
			createButton( parent, BROWSE_WORKSPACE_ID, "Workspace...", false );
			createButton( parent, BROWSE_FILESYSTEM_ID, "File System...", false );
		}

		protected void buttonPressed(int buttonId) {
			// Retrieves the path if one of the browse buttons is pressed.
			String	path = null;
			switch( buttonId ) {
				case BROWSE_FILESYSTEM_ID:
					path = Convenience.browseFileSystemForDirectory( getShell(), setting.getOwner(), getText().getText() );
					break;
				
				case BROWSE_WORKSPACE_ID:
					path = Convenience.browseWorkspaceForDirectory( getShell(), setting.getOwner(), getText().getText() );
					break;
					
				default:
					super.buttonPressed(buttonId);
			}
			
			// Updates the input text if a path has been retrieved.
			if( path != null ) {
				getText().setText( path );
			}
		}		
	}

	protected String editValueCompound( Shell parent, Setting setting, String compound ) {
		MyInputDialog	dialog = new MyInputDialog( parent, setting, compound );
		if( dialog.open() == IDialogConstants.OK_ID ) {
			return dialog.getValue();
		}
		else {
			return null;
		}
	}

}
