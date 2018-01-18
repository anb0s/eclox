/*******************************************************************************
 * Copyright (C) 2003-2004, 2013, Guillaume Brocker
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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eclox.core.ListSeparateMode;
import eclox.core.TagFormat;

/**
 * Implements the plugin preference page.
 *
 * @author gbrocker
 */
public class PreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {
    /**
     * Constructor.
     */
    public PreferencePage() {
        super(GRID);
    }

    public void init(IWorkbench workbench) {
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
    }

    /**
     * Creates the preference page fields.
     */
    protected void createFieldEditors() {
        Composite rootControl = getFieldEditorParent();

        // Create the controls.

        BooleanFieldEditor escapeValueStrings = new BooleanFieldEditor(IPreferences.HANDLE_ESCAPED_VALUES,
                "Handle escapes for \" and \\ in value strings", rootControl);
        escapeValueStrings.setPreferenceStore(getPreferenceStore());
        addField(escapeValueStrings);

        IntegerFieldEditor historySize = new IntegerFieldEditor(IPreferences.BUILD_HISTORY_SIZE, "Build history size:",
                rootControl);
        historySize.setPreferenceStore(getPreferenceStore());
        historySize.setValidRange(1, 100);
        addField(historySize);

        RadioGroupFieldEditor autoSaveField = new RadioGroupFieldEditor(IPreferences.AUTO_SAVE,
                "Save all modified files before building", 1,
                new String[][] { { IPreferences.AUTO_SAVE_NEVER, IPreferences.AUTO_SAVE_NEVER },
                        { IPreferences.AUTO_SAVE_ALWAYS, IPreferences.AUTO_SAVE_ALWAYS },
                        { IPreferences.AUTO_SAVE_ASK, IPreferences.AUTO_SAVE_ASK }, },
                rootControl, true);
        autoSaveField.setPreferenceStore(getPreferenceStore());
        addField(autoSaveField);

        int lineSepLength = LineSeparator.values().length;
        String[][] lineSepNames = new String[lineSepLength][2];
        for(int i=0;i<lineSepLength;i++) {
            lineSepNames[i][0] = LineSeparator.values()[i].getName();
            lineSepNames[i][1] = LineSeparator.values()[i].name();
        }
        RadioGroupFieldEditor lineSeparatorField = new RadioGroupFieldEditor(IPreferences.LINE_SEPARATOR,
                "Line separator", 1, lineSepNames, rootControl, true);
        lineSeparatorField.setPreferenceStore(getPreferenceStore());
        addField(lineSeparatorField);

        int listSepModeLength = ListSeparateMode.values().length;
        String[][] listSepModeNames = new String[listSepModeLength][2];
        for(int i=0;i<listSepModeLength;i++) {
            listSepModeNames[i][0] = ListSeparateMode.values()[i].getName();
            listSepModeNames[i][1] = ListSeparateMode.values()[i].name();
        }
        RadioGroupFieldEditor listSepModeField = new RadioGroupFieldEditor(IPreferences.LIST_SEPARATE_MODE,
                "List separate mode", 1, listSepModeNames, rootControl, true);
        listSepModeField.setPreferenceStore(getPreferenceStore());
        addField(listSepModeField);

        int tagFormatLength = TagFormat.values().length;
        String[][] tagFormatNames = new String[tagFormatLength][2];
        for(int i=0;i<tagFormatLength;i++) {
            tagFormatNames[i][0] = TagFormat.values()[i].getName();
            tagFormatNames[i][1] = TagFormat.values()[i].name();
        }
        RadioGroupFieldEditor tagFormatField = new RadioGroupFieldEditor(IPreferences.TAG_FORMAT,
                "TAG format", 1, tagFormatNames, rootControl, true);
        tagFormatField.setPreferenceStore(getPreferenceStore());
        addField(tagFormatField);

    }

}
