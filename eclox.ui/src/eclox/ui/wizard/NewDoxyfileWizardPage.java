/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - images handling
 *
 ******************************************************************************/

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
		setImageDescriptor(Plugin.getImageDescriptor(Images.DOXYFILE_WIZARD.getId()));
	}

	/**
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getNewFileLabel()
	 */
	protected String getNewFileLabel() {
		return "Doxyfile &name:";
	}

}
