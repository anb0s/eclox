/*******************************************************************************
 * Copyright (C) 2003-2005, 2013, Guillaume Brocker
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

package eclox.ui.editor.advanced;

import java.util.HashMap;
import java.util.Map;

import eclox.core.doxyfiles.Setting;
import eclox.ui.Plugin;
import eclox.ui.editor.editors.BooleanEditor;
import eclox.ui.editor.editors.DirectoryEditor;
import eclox.ui.editor.editors.DirectoryListEditor;
import eclox.ui.editor.editors.FileEditor;
import eclox.ui.editor.editors.PathListEditor;
import eclox.ui.editor.editors.TextEditor;
import eclox.ui.editor.editors.TextListEditor;

/**
 * An instance of this class registers all setting editor classes by setting type.
 *
 * @author gbrocker
 */
public class EditorClassRegister {

    /**
     * The map registering all editor classes.
     */
    private Map<String, Class<?>> register = new HashMap<String, Class<?>>();

    /**
     * Constructor.
     */
    public EditorClassRegister() {
        register.put("file", FileEditor.class);
        register.put("directory", DirectoryEditor.class);
        register.put("text", TextEditor.class);
        register.put("boolean", BooleanEditor.class);
        register.put("text list", TextListEditor.class);
        register.put("directory list", DirectoryListEditor.class);
        register.put("path list", PathListEditor.class);
    }

    /**
     * Retrieves a class for the specified setting.
     *
     * @param	setting	a setting for which an editor must be retrieved
     *
     * @return	a setting editor class
     */
    public Class<?> find(Setting setting) {
        // Retrieves the editor class for that type
        String type = setting.getProperty(Setting.TYPE);
        Class<?> result = (Class<?>) register.get(type);

        // Little fallback if no matching editor class was found.
        if (result == null) {
            Plugin.log(setting.getIdentifier() + ": missing or wrong TYPE property.");
            result = TextEditor.class;
        }

        return result;
    }

}
