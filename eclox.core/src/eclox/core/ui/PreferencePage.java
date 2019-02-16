/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
 * Copyright (C) 2015-2019, Andre Bossert
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

package eclox.core.ui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import eclox.core.Plugin;

/**
 * Implements the preferences for the core eclox plug-in.
 *
 * @author gbrocker
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "eclox.core.PreferencePage"; ///< Holds the identifier of the property page.

    public PreferencePage() {
        super(GRID);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(
                new ScopedPreferenceStore(InstanceScope.INSTANCE, Plugin.getDefault().getBundle().getSymbolicName()));
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors() {
        // Creates all control instances.
        Label doxygenLabel = new Label(getFieldEditorParent(), SWT.WRAP);
        DefaultDoxygenFieldEditor doxygen = new DefaultDoxygenFieldEditor(getFieldEditorParent());

        // Configures field editors.
        addField(doxygen);

        // Configures the default doxygen label.
        GridData doxygenLabelData = new GridData();

        doxygenLabelData.horizontalSpan = 3;
        doxygenLabelData.horizontalAlignment = SWT.FILL;
        doxygenLabel.setText("Choose among available doxygen versions, the one you would like to use.");
        doxygenLabel.setLayoutData(doxygenLabelData);
    }

}
