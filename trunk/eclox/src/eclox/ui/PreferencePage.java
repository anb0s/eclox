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


package eclox.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Implements the plugin preference page.
 * 
 * @author gbrocker
 */
public class PreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * Constructor.
	 */
	public PreferencePage() {
		super( GRID );
	}
	
	public void init( IWorkbench workbench ) {
	}
	
	/**
	 * Creates the preference page fields.
	 */
	protected void createFieldEditors() {
		Composite			rootControl = getFieldEditorParent();
		IPreferenceStore	preferenceStore = Plugin.getDefault().getPreferenceStore();
		
		// Create the controls.
		FileFieldEditor	doxPath = new FileFieldEditor(eclox.preferences.Names.COMPILER_PATH, "Compiler path:", true, rootControl);
		doxPath.setPreferenceStore(preferenceStore);
		addField(doxPath);
		
		IntegerFieldEditor buildHistorySize = new IntegerFieldEditor(eclox.preferences.Names.BUILD_HISTORY_SIZE, "Maximal build history size:", rootControl);
		buildHistorySize.setPreferenceStore(preferenceStore);
		buildHistorySize.setValidRange(1, 15);
		buildHistorySize.setErrorMessage("Value must be an integer between 1 and 15.");
		addField(buildHistorySize);
		
		BooleanFieldEditor saveAll = new BooleanFieldEditor( eclox.preferences.Names.AUTO_SAVE, "Save changes before launching compilation.", BooleanFieldEditor.DEFAULT, rootControl );
		saveAll.setPreferenceStore( preferenceStore );
		addField( saveAll );
	}

	/**
	 * Retrieves the preference store for this page.
	 *
	 * @return	The preference store instance for this page.
	 */
	protected IPreferenceStore doGetPreferenceStore() {
		return Plugin.getDefault().getPreferenceStore();
	}
}
