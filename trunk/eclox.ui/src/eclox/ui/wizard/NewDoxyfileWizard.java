/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003,2004,2007 Guillaume Brocker
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

package eclox.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PreferencesUtil;

import eclox.core.doxygen.Doxygen;
import eclox.core.doxygen.InvokeException;
import eclox.core.doxygen.RunException;

/**
 * Implement the new file wizard extension to provide a way to create
 * new doxyfiles.
 * 
 * @author gbrocker
 */
public class NewDoxyfileWizard extends Wizard implements INewWizard {
	
	/**
	 * The wizard page used to get the file name.
	 */
	private NewDoxyfileWizardPage m_page;
	
	/**
	 * Initialize the new doxyfile wizard.
	 */
	public void init( IWorkbench workbench, IStructuredSelection selection ) {
		m_page = new NewDoxyfileWizardPage( selection );
		addPage( m_page );
		setWindowTitle( "New Doxygen Configuration" );
	}
	
	/**
	 * Make the wizard finish.
	 * 
	 * @return	true if the wizard has completed, false otherwise.
	 */
	public boolean performFinish() {
		for(;;)	{
			// Creates the doxyfile resource.
			IFile	doxyfile = createFile( m_page.getContainerFullPath(), m_page.getFileName() );
			
			// Warn user if the doxyfile exists.
			if( doxyfile.exists() ) {
				MessageDialog.openWarning(getShell(), "Resource Exists", "The resource " + doxyfile.getFullPath().toString() + " already exists !" );
				return false;
			}
	
			// Creates the effective resource file.
			try {
				Doxygen.getDefault().generate( doxyfile );
				return true;
			}
			// Doxygen returned an error.
			catch( RunException runException ) {
				MessageDialog.openError(getShell(), "Doxygen Error", "An error occured while running doxygen. " + runException.toString());
				return true;
			}
			// Doxygen was impossible to run. 
			catch( InvokeException invokeException ) {
				// Asks the user if he wants to edit the preferences to solve the problem.
				boolean	edit = MessageDialog.openQuestion(getShell(), "Doxygen Not Found", "Eclox was not able to run doxygen. Doxygen is either missing or eclox is not properly configured to use it.\n\nWould you like to edit preferences now ?" );
				if( ! edit ) {
					return true;
				}
	
				// Allows the user to edit the preferences and eventually launch doxygen again.
				String[]	filter = { eclox.core.ui.PreferencePage.ID };
				int			edited = PreferencesUtil.createPreferenceDialogOn(getShell(), eclox.core.ui.PreferencePage.ID, filter, null).open();
				if( edited == Window.OK ) {
					continue;
				}
				
				// Stops the wizard.
				return true;
			}
		}
	}
	
	/**
	 * Create the resource file.
	 * 
	 * @param containerPath	The path to the container for the resource to create.
	 * @param fileName		A string containnig the name of the file resource to create.
	 * 
	 * @return	The created file resource. The file system's file hasn't been created at this step.
	 */
	private IFile createFile( IPath containerPath, String fileName ) {
		IFile	result;
		IPath	filePath;
		String	fileExtension;
		
		filePath = containerPath.append( fileName );
		fileExtension = filePath.getFileExtension(); 
		if( (fileExtension == null || fileExtension.compareToIgnoreCase( "Doxyfile" ) != 0) && fileName.compareToIgnoreCase("Doxyfile") != 0 ) {
			filePath = filePath.addFileExtension( "Doxyfile" );
		}
		result = ResourcesPlugin.getWorkspace().getRoot().getFile( filePath );
		return result;
	}
}
