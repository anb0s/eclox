/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
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

package eclox.ui.editor.basic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eclox.core.doxyfiles.Setting;
import eclox.ui.editor.editors.AbstractEditor;

/**
 * Base implementation of for multi editors.
 *
 * @author Guillaume Brocker
 */
public abstract class MultiEditor extends AbstractEditor {

    /**
     * symbolic constant value for yes
     */
    private static final String YES = "YES";

    /**
     * symbolic constant value for no
     */
    private static final String NO = "NO";

    protected class State {
        private String name;
        private Set<Setting> selectedSettings = new HashSet<Setting>();
        private Set<Setting> deselectedSettings = new HashSet<Setting>();

        State(String name) {
            this.name = name;
        }

        void addSettingToSelect(Setting setting) {
            selectedSettings.add(setting);
            deselectedSettings.remove(setting);
        }

        void addSettingToDeselect(Setting setting) {
            if (selectedSettings.contains(setting) == false) {
                deselectedSettings.add(setting);
            }
        }

        String getName() {
            return name;
        }

        boolean wantsSelection() {
            boolean wanted = true;
            Iterator<Setting> i;

            // Updates the selection according to the value of settings owned by the state.
            i = selectedSettings.iterator();
            while (i.hasNext()) {
                Setting setting = (Setting) i.next();

                wanted = wanted && setting.getValue().equals(YES);
            }

            // Updates the selection according to the value of settings owned by the state.
            i = deselectedSettings.iterator();
            while (i.hasNext()) {
                Setting setting = (Setting) i.next();

                wanted = wanted && setting.getValue().equals(NO);
            }

            // Job's done.
            return wanted;
        }

        void commit() {
            Iterator<Setting> i;

            i = selectedSettings.iterator();
            while (i.hasNext()) {
                Setting setting = (Setting) i.next();

                setting.setValue(YES);
            }

            i = deselectedSettings.iterator();
            while (i.hasNext()) {
                Setting setting = (Setting) i.next();

                setting.setValue(NO);
            }
        }

    }

    /**
     * the collection of managed states
     */
    protected State[] states;

    /**
     * the state being selected
     */
    private State selection;

    /**
     * a boolean telling if the editor is dirty
     */
    private boolean dirty = false;

    /**
     * Creates a new multi editor instance
     *
     * @param states	an array containing the name of the states to create
     */
    public MultiEditor(String[] states) {
        this.states = new State[states.length];
        for (int i = 0; i != states.length; ++i) {
            this.states[i] = new State(states[i]);
        }
    }

    /**
     * Adds the given setting to a given state
     *
     * @param	state	the name of a state
     * @param	setting	the setting to add to the given state
     */
    public void addSetting(String state, Setting setting) {
        if (setting != null) {
            for (int i = 0; i != states.length; ++i) {
                if (states[i].name.equals(state)) {
                    states[i].addSettingToSelect(setting);
                } else {
                    states[i].addSettingToDeselect(setting);
                }
            }
        }
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#commit()
     */
    public void commit() {
        // Commits the selected state.
        if (selection != null) {
            selection.commit();
        }
        dirty = false;
        fireEditorChanged();
    }

    /**
     * Retrieves the selected state of the editor
     *
     * @return	a string representing the selected state, or null if none
     */
    public String getSelection() {
        return (selection != null) ? selection.getName() : null;
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#isDirty()
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#isStale()
     */
    public boolean isStale() {
        // Looks for the state that wants the selection.
        State wantedSelection = null;
        for (int i = 0; i != states.length; ++i) {
            if (states[i].wantsSelection()) {
                wantedSelection = states[i];
                break;
            }
        }

        return wantedSelection != selection;
    }

    /**
     * Sub-classes must call this method first, in order to refresh the current state
     * and then refresh the user interface controls, according to the current state.
     *
     * @see eclox.ui.editor.editors.IEditor#refresh()
     */
    public void refresh() {
        selection = null;
        dirty = false;

        // Searches the states that wants to be selected
        for (int i = 0; i != states.length; ++i) {
            if (states[i].wantsSelection()) {
                selection = states[i];
                break;
            }
        }
        fireEditorChanged();
    }

    /**
     * Retrieves the selected state of the multi editor.
     *
     * @return	a state or null when none
     */
    protected State getSelectionAsState() {
        return selection;
    }

    /**
     * Retrieves the state for the given name
     *
     * @name	a string containing a state name
     *
     * @return	the matching state or null when none
     */
    protected State getState(String name) {
        State found = null;

        for (int i = 0; i != states.length; ++i) {
            if (states[i].getName().equals(name)) {
                found = states[i];
                break;
            }
        }
        return found;
    }

    /**
     * Selectes the given state
     *
     * @param	state	a string containing a state name
     */
    protected void selectState(String state) {
        State candidate = getState(state);
        if (candidate != null) {
            selection = candidate;
            dirty = true;
            fireEditorChanged();
        }
    }

}
