/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2004 Guillaume Brocker

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

package eclox.ui.plugin;

import org.eclipse.jface.preference.*;

/**
 * Defines all preference names.
 * 
 * @author gbrocker
 */
public final class Preferences {
	/**
	 * Specifies the doxygen compiler path constant name.
	 */
	public static final String COMPILER_PATH = "CompilerPath";
	
	/**
	 * Specifies the doxyfile history length name.
	 */
	public static final String BUILD_HISTORY_SIZE = "BuildHistorySize";
	
	/**
	 * Specifies the auto save document constant name.
	 */
	public static final String AUTO_SAVE = "AutoSave";
	
	/**
	 * Initialize default preference values.
	 * 
	 * @param	store	The preference store where the settings sould be initialized.
	 */	
	public static void setDefault( IPreferenceStore store ) {
		store.setDefault(COMPILER_PATH, "");
		store.setDefault(BUILD_HISTORY_SIZE, 4);
		store.setDefault(AUTO_SAVE, true);
	}
}
