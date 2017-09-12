/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2016, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Refactoring of deprecated API usage
 *
 ******************************************************************************/

package eclox.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

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
        // get the actual default preference store
        final String PLUGIN_ID = Plugin.getDefault().getBundle().getSymbolicName();
        IEclipsePreferences defaultNode = DefaultScope.INSTANCE.getNode(PLUGIN_ID);
        // set defaults
        setDefaults(defaultNode);
    }

    private void setDefaults(IEclipsePreferences defaultNode) {
        defaultNode.put(IPreferences.DEFAULT_DOXYGEN, new DefaultDoxygen().getIdentifier());
        defaultNode.put(IPreferences.CUSTOM_DOXYGENS, "");
    }

}
