/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006 Guillaume Brocker
 *
 * This file is part of eclox.
 *
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
 */

package eclox.core.ui;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import eclox.core.IPreferences;
import eclox.core.Plugin;
import eclox.core.doxygen.Doxygen;

/**
 * Implements the preferences for the core eclox plugin.
 * 
 * @author gbrocker
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Implements a file field editor for the doxygen command path that will validate
	 * the doxygen path.
	 *
	 *	@author	Guillaume Brocker
	 */
	private class DoxygenPathEditor extends FileFieldEditor
	{
		public Label	messageLabel; ///< a label control used to show additionnal messages.
		
		/**
		 * Contructor
		 * 
		 * @param parent	the parent control
		 */
		public DoxygenPathEditor( Composite parent ) {
			super( IPreferences.DOXYGEN_COMMAND, "Path:", true, parent );
			
			setValidateStrategy( VALIDATE_ON_KEY_STROKE );
		}
		
		/**
		 * Refreshes the valid state of the editor.
		 */
		protected boolean checkState() {
			try {
				// Resets the message label.
				if( messageLabel != null )
				{
					messageLabel.setText( new String() );
				}
				
				// Calls default validity check.
				if( super.checkState() == false ) {
					return false;
				}
				
				// Retrieves the edited doxygen path
				String	path = getStringValue();
				if( path.length() == 0 ) {
					path = Doxygen.DEFAULT_DOXYGEN_COMMAND;
				}
				
				// Retrieves the doxygen version.
				String version = Doxygen.getVersion( path );
				clearErrorMessage();
				if( messageLabel != null )
				{
					messageLabel.setText( "Detected Doxygen " + version );
				}
				return true;
			}
			catch( Throwable t ) {
				showErrorMessage( "Doxygen was not found at the given path." );
				return false;
			}
		}
		
	}
	
	public PreferencePage()
	{
		super( GRID );
	}
	
	public void init(IWorkbench workbench)
	{
		setPreferenceStore( new ScopedPreferenceStore( new InstanceScope(), Plugin.getDefault().getBundle().getSymbolicName() ) );
	}

	protected void createFieldEditors()
	{
		// Create the doxygen path editor.
		DoxygenPathEditor	doxygenPath = new DoxygenPathEditor( getFieldEditorParent() );
		doxygenPath.setPreferenceStore( getPreferenceStore() );
		addField(doxygenPath);
		
		// Assignes the message label to the editor.
		Label		messageLabel = new Label( getFieldEditorParent(), 0 );
		GridData	gridData = new GridData();
		
		gridData.horizontalSpan = 3;
		messageLabel.setLayoutData( gridData );
		doxygenPath.messageLabel = messageLabel;
	}

}
