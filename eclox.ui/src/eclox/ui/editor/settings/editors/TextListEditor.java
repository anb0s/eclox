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

package eclox.ui.editor.settings.editors;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import eclox.core.doxyfiles.Setting;

/**
 * Implements a list editor that handle value compounds as simple text
 * 
 * @author gbrocker
 */
public class TextListEditor extends ListEditor {
	
	/**
	 * Implements an input validator for the input dialog used to edit value compunds.
	 */
	private class MyInputValidator implements IInputValidator {

		public String isValid(String newText) {
			if( newText.length() == 0 ) {
				return "Empty value is not allowed.";
			}
			else if( newText.contains( "\"" ) ) {
				return "\" is a forbidden character.";
			}
			else {
				return null;
			}
		}
		
	}
	
	protected String editValueCompound(Shell parent, Setting setting, String compound) {
		// Creates the edition dialog.
		InputDialog	dialog = new InputDialog(
				parent,
				"Value Edition",
				"Enter the new text for the value.",
				compound,
				new MyInputValidator() );
		
		// Lauches the value edition
		if( dialog.open() == InputDialog.OK ) {			
			return dialog.getValue();
		}
		else {
			return null;
		}
	}

}
