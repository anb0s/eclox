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

package eclox.core;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import eclox.core.Plugin;
import eclox.core.doxygen.DefaultDoxygen;


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
		Preferences	preferences = Plugin.getDefault().getPluginPreferences();
		
		preferences.setDefault( IPreferences.DEFAULT_DOXYGEN, new DefaultDoxygen().getIdentifier() );
		preferences.setDefault( IPreferences.CUSTOM_DOXYGENS, "" );
	}

}
