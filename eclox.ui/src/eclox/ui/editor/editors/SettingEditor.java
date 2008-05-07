// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2008, Guillaume Brocker
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

package eclox.ui.editor.editors;

import eclox.core.doxyfiles.Setting;

public abstract class SettingEditor extends AbstractEditor {

	private Setting	input = null;	///< References the editor's input.
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#dispose()
	 */
	public void dispose() {
		input = null;
	}
	
	/**
	 * Retrieves the editor input.
	 * 
	 * @return	the current input of the editor
	 */
	public Setting getInput() {
		return input;
	}
	
	/**
	 * Tells if the editor has been assigned a setting.
	 * 
	 * @return	true or false
	 */
	public boolean hasInput() {
		return input != null;
	}
	
	/**
	 * Sets the editor input.
	 * 
	 * @param	input	the new input of the editor, null if none
	 */
	public void setInput(Setting input) {
		this.input = input;
	}

}
