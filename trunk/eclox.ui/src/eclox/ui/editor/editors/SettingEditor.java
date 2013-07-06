/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
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
