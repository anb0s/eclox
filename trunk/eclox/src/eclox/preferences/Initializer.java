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

package eclox.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import eclox.ui.Plugin;

/**
 * Implements the prefenrence initializer for the plugin.
 * 
 * @author gbrocker
 */
public class Initializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		Preferences	preferences = Plugin.getDefault().getPluginPreferences();
		
		preferences.setDefault( Names.COMPILER_PATH, "" );
		preferences.setDefault( Names.BUILD_HISTORY_CONTENT, "" );
		preferences.setDefault( Names.BUILD_HISTORY_SIZE, 4 );
		preferences.setDefault( Names.AUTO_SAVE, Values.AUTO_SAVE_ASK );
	}

}
