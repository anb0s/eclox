/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2018, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - #215: add support for line separator
 *                   - #212: add support for multiple lines (lists) concatenated by backslash (\)
 *                   - #214: add support for TAG and VALUE format
 *
 ******************************************************************************/

package eclox.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eclox.core.ListSeparateMode;
import eclox.core.TagFormat;
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
        IPreferenceStore preferences = Plugin.getDefault().getPreferenceStore();
        preferences.setDefault(IPreferences.BUILD_HISTORY_SIZE, 5);
        preferences.setDefault(IPreferences.AUTO_SAVE, IPreferences.AUTO_SAVE_ASK);
        preferences.setDefault(IPreferences.HANDLE_ESCAPED_VALUES, true);
        preferences.setDefault(IPreferences.LINE_SEPARATOR, LineSeparator.lineSeparatorSystem.name());
        preferences.setDefault(IPreferences.LIST_SEPARATE_MODE, ListSeparateMode.listSeparateModeDoNotChange.name());
        preferences.setDefault(IPreferences.TAG_FORMAT, TagFormat.tagFormatDoNotChange.name());
    }

}
