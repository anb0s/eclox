/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2008 Guillaume Brocker
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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import eclox.ui.Images;
import eclox.ui.Plugin;

/**
 * @author gbrocker
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NewDoxyfileWizardPage extends WizardNewFileCreationPage {
	
	/**
	 * Retrieves the initial doxyfile name relative to the given object that
	 * is supposed to be a resource.
	 * 
	 * If the object is not an IResourec instance, and adapter is searched for it.
	 */
	private static String getInitialFileName( Object object ) {
		IResource resource;
		
		// Skip null objects
		if( object == null ) {
			resource = null;
		}
		// Try the direct convertion to a IResource 
		else if( object instanceof IResource ) {
			resource = (IResource) object;
		}
		// Try to find an adapter
		else {
			resource = (IResource) org.eclipse.core.runtime.Platform.getAdapterManager().getAdapter(object, IResource.class);				
		}

		// Finally, gets the project name for the resource (if one has been found).
		return (resource != null) ? (resource.getProject().getName() + ".doxyfile") : new String();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param selection	The current selection object.
	 */
	public NewDoxyfileWizardPage(IStructuredSelection selection) {
		super("page", selection);
		setTitle("Doxygen Configuration");
		setDescription("Creates a new Doxygen configuration file.");
		setFileName( selection != null ? getInitialFileName(selection.getFirstElement()) : new String() );
		setImageDescriptor( Plugin.getImageDescriptor(Images.DOXYFILE_WIZARD));		
	}

	/**
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getNewFileLabel()
	 */
	protected String getNewFileLabel() {
		return "Doxyfile &name:";
	}
	
}
