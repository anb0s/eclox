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

package eclox.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eclox.ui.IPreferences;


/**
 * Implements the prefenrence initializer for the plugin.
 * 
 * @author gbrocker
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore	preferences = Plugin.getDefault().getPreferenceStore();
		
		preferences.setDefault( IPreferences.BUILD_HISTORY_SIZE, 5 );
		preferences.setDefault( IPreferences.AUTO_SAVE, IPreferences.AUTO_SAVE_ASK );
		preferences.setDefault( IPreferences.HANDLE_ESCAPED_VALUES, true );
	}

}
