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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import eclox.core.Plugin;

/**
 * Implements the preferences for the core eclox plugin.
 * 
 * @author gbrocker
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
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
		// Creates all control instances.
		Label						doxygenLabel	= new Label( getFieldEditorParent(), SWT.WRAP );
		DefaultDoxygenFieldEditor	doxygen			= new DefaultDoxygenFieldEditor( getFieldEditorParent() );
		
		
		// Configures field editors.
		addField( doxygen );
		
		
		// Configures the default doxygen label.
		GridData	doxygenLabelData = new GridData();
		
		doxygenLabelData.horizontalSpan = 3;
		doxygenLabelData.horizontalAlignment = SWT.FILL;
		doxygenLabel.setText( "Choose among available doxygen versions, the one you would like to use." );
		doxygenLabel.setLayoutData( doxygenLabelData );
	}
	
}
