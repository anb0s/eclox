/*******************************************************************************
 * Copyright (C) 2003, 2004, 2007, 2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Add ability to use Doxyfile not in project scope
 *                   - fixed: context menu entry shown unnecessarily
 *
 ******************************************************************************/

package eclox.ui.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.ResourceCollector;
import eclox.ui.DoxyfileSelector;
import eclox.ui.Plugin;
import eclox.ui.ResourceType;

/**
 * Implement a pop-up menu action delegate that will allow to
 * launch doxygen builds from resources' contextual menu.
 *
 * @author gbrocker
 */
public class BuildPopupActionDelegate implements IObjectActionDelegate {

	private IResource		resource;	///< References the resource that is under the contextual menu.
	private IWorkbenchPart	targetPart;	///< References the part where the action taks place.

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			IFile	doxyIFile = null;
			// If there is a resource, it is either a doxyfile
			// and if not, we prompt the user to get one.
			if( resource != null ) {
			    doxyIFile = Doxyfile.isDoxyfile(resource) ? (IFile) resource : DoxyfileSelector.open(resource);
			}
			if( doxyIFile != null ) {
				Plugin.getDefault().getBuildManager().build( new Doxyfile(doxyIFile, null) );
			}
		}
		catch(Throwable throwable) {
		    if (targetPart != null) {
		        MessageDialog.openError(targetPart.getSite().getShell(), "Unexpected Error", throwable.toString());
		    }
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	    this.resource = getDoxygenResourceRoot(selection, this.targetPart);
	    if (action != null) {
	        action.setEnabled(this.resource != null);
	    }
	}

	static public IResource getDoxygenResourceForType(ISelection selection, IWorkbenchPart targetPart, ResourceType resType) {
	    IResource resource = null;
	    if (resType != ResourceType.resourceTypeUnknown) {
    	    resource = getDoxygenResourceRoot(selection, targetPart);
    	    if (resource != null) {
    	        if (resType != ResourceType.resourceTypeFileOrDirectory) {
            	    boolean isDoxyfile = Doxyfile.isDoxyfile(resource);
            	    if (resType == ResourceType.resourceTypeFile) {
            	        if (!isDoxyfile) {
            	            resource = null;
            	        }
            	    }
            	    else if (resType == ResourceType.resourceTypeDirectory) {
                        if (isDoxyfile) {
                            resource = null;
                        }
            	    }
    	        }
    	    }
	    }
	    return resource;
	}

    static public IResource getDoxygenResourceRoot(ISelection selection, IWorkbenchPart targetPart) {
        IResource resource = null;
        IStructuredSelection strSelection = (IStructuredSelection) selection;
        try {
            if( strSelection.size() == 1 ) {
                Object object = strSelection.getFirstElement();
                resource = (IResource) Platform.getAdapterManager().getAdapter(object, IResource.class);
                if( resource != null && resource.isAccessible() ) {
                    ResourceCollector collector = ResourceCollector.run(resource);
                    if (!collector.isEmpty()) {
                        // if there is only one collected doxyfile, then assigns that doxyfile as the current resource
                        if (collector.getSize() == 1) {
                            resource = collector.getFirst();
                        }
                        // else: just use the root (container) to the two or more doxyfiles for later selection by user
                    } else {
                        // resets the resource when a doxyfile has not been found
                        resource = null;
                    }
                } else {
                    // resets the resource when it was not accessible
                    resource = null;
                }
            }
        }
        catch(Throwable throwable) {
            if (targetPart != null) {
                MessageDialog.openError(targetPart.getSite().getShell(), "Unexpected Error", throwable.toString());
            }
        }
        return resource;
    }

}
