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
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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
		setPreferenceStore( Plugin.getDefault().getPreferenceStore() );
	}
	
	/**
	 * Creates the preference page fields.
	 */
	protected void createFieldEditors() {
		Composite	rootControl = getFieldEditorParent();
	
		// Create the controls.
		
		BooleanFieldEditor escapeValueStrings = new BooleanFieldEditor(IPreferences.HANDLE_ESCAPED_VALUES, "Handle escapes for \" and \\ in value strings", rootControl);
		escapeValueStrings.setPreferenceStore( getPreferenceStore() );
		addField( escapeValueStrings );
		
		IntegerFieldEditor historySize = new IntegerFieldEditor(IPreferences.BUILD_HISTORY_SIZE, "Build history size:", rootControl);
		historySize.setPreferenceStore( getPreferenceStore() );
		historySize.setValidRange( 1, 100 );
		addField( historySize );

		RadioGroupFieldEditor autoSave = new RadioGroupFieldEditor(
			IPreferences.AUTO_SAVE,
			"Save all editors before building",
			1,
			new String[][] {
				{IPreferences.AUTO_SAVE_NEVER, IPreferences.AUTO_SAVE_NEVER},
				{IPreferences.AUTO_SAVE_ALWAYS, IPreferences.AUTO_SAVE_ALWAYS},
				{IPreferences.AUTO_SAVE_ASK, IPreferences.AUTO_SAVE_ASK},
			},
			rootControl,
			true );
		autoSave.setPreferenceStore( getPreferenceStore() );
		addField( autoSave );
	}

}
