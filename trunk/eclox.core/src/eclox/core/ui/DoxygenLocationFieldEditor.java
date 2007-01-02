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

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eclox.core.IPreferences;
import eclox.core.doxygen.LocalDoxygen;

/**
 * Implements an specialized field editor that provides a status information
 * about doxygen found at the given location
 *  
 * @author Guillaume Brocker
 */
public class DoxygenLocationFieldEditor extends DirectoryFieldEditor {
	
	/**
	 * the labal control that shows the status text
	 */
	Label	status;

	/**
	 * @see org.eclipse.jface.preference.DirectoryFieldEditor#doCheckState()
	 */
	protected boolean doCheckState() {
		boolean			result	= super.doCheckState();
		LocalDoxygen	doxygen	= new LocalDoxygen( getStringValue() );
		final String	version = doxygen.getVersion();
			
		if( version != null ) {
			status.setText( "Doxygen " + version + " found.");
			status.setFont( JFaceResources.getDialogFont() );
		}
		else {
			status.setText( "Doxygen not found. Check location!");
			status.setFont( JFaceResources.getBannerFont() );
		}		
		
		return result;
	}

	/**
	 * @see org.eclipse.jface.preference.StringButtonFieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		// Pre-condition
		assert( status != null );
		
		
		super.adjustForNumColumns(numColumns);
		
		
		// Updates the status control layout data.
		GridData	statusData = (GridData) status.getLayoutData();
		
		statusData.horizontalSpan = numColumns;
	}

	/**
	 * @see org.eclipse.jface.preference.StringButtonFieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		// Pre-condition
		assert( status == null );
		
		super.doFillIntoGrid(parent, numColumns);
		
		
		// Creates the status label.
		GridData	statusData = new GridData();
		
		statusData.verticalAlignment = SWT.FILL;
		statusData.horizontalAlignment = SWT.FILL;
		statusData.horizontalSpan = numColumns;
		
		status = new Label( parent, SWT.WRAP );
		status.setLayoutData( statusData );		
	}
	
	
	public DoxygenLocationFieldEditor( Composite parent ) {
		super( IPreferences.DOXYGEN_COMMAND, "Local Doxygen Location: ", parent );
	}

}
