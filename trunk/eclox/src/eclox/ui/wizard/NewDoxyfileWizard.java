/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003 Guillaume Brocker

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

package eclox.ui.wizard;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import eclox.Doxygen;
import eclox.ui.Plugin;

/**
 * Implement the new file wizard extension.
 * 
 * @author gbrocker
 */
public class NewDoxyfileWizard extends Wizard implements INewWizard {
	/**
	 * The wizard selection.
	 */
	private IStructuredSelection m_selection;
	
	/**
	 * The wizard page used to get the file name.
	 */
	private NewDoxyfileWizardPage m_page;
	
	/**
	 * Initialize the new doxyfile wizard.
	 */
	public void init( IWorkbench workbench, IStructuredSelection selection ) {
		m_selection = selection;
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
		boolean	result;
		
		try {
			IFile	doxyfile;

			doxyfile = createFile( m_page.getContainerFullPath(), m_page.getFileName() );
			if( doxyfile.exists() ) {
				Plugin.getDefault().showError( "The resource " + doxyfile.getFullPath().toString() + " already exists !" );
				result = false;	
			}
			else {
				Doxygen.generate( doxyfile );
				result = true;
			}	
		}
		catch( IOException ioException ) {
			Plugin.getDefault().showError( ioException );
			result = false;
		}
		catch( InterruptedException interruptedException ) {
			Plugin.getDefault().showError( interruptedException );
			result = false;
		}
		catch( CoreException coreException ) {
			Plugin.getDefault().showError( coreException );
			result = false;
		}
		
		return result;
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
		if( fileExtension == null || fileExtension.compareToIgnoreCase( "cfg" ) != 0 ) {
			filePath = filePath.addFileExtension( "cfg" );
		}
		result = ResourcesPlugin.getWorkspace().getRoot().getFile( filePath );
		return result;
	}
}
