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

package eclox.resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import eclox.ui.Plugin;
import eclox.util.ListenerManager;

/**
 * Implements the doxyfile listener manager
 * 
 * @author gbrocker
 */
public class DoxyfileListenerManager extends ListenerManager {
	/**
	 * Implements a resource change listener that will allow the doxyfile listener manager
	 * notify its registered listeners.
	 */
	private class ResourceChangeListener implements IResourceChangeListener {
		/**
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				if(event.getType() == IResourceChangeEvent.POST_CHANGE) {
					event.getDelta().accept(new ResourceDeltaVisitor());				
				}
			}
			catch(Throwable throwable) {
				Plugin.getDefault().showError(throwable);
			}
		}
	}
	
	/**
	 * Implements a resource delta visitor that will report any changes on doxyfiles.
	 */
	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {
		/**
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if(Doxyfile.isDoxyfile(resource) == true) {
				IFile	doxyfile = (IFile) resource;
				
				if((delta.getKind() & IResourceDelta.REMOVED) != 0) {
					fireDoxyfileRemovedEvent(doxyfile);
				}
			}
			return true;
		}		
	}
	
	/**
	 * The singleton instance.
	 */
	private static DoxyfileListenerManager instance;
	
	/**
	 * Constructor.
	 */
	private DoxyfileListenerManager() {
		super(DoxyfileListener.class);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceChangeListener());
	}
	
	/**
	 * Registers a new doxyfile listener.
	 * 
	 * @param	listener	a new listener to register
	 */
	public void addDoxyfileListener(DoxyfileListener listener) {
		super.addListener(listener);
	}
	
	/**
	 * Retrieve the default instance.
	 *
	 * @return	the default listener manager instance
	 */
	public static DoxyfileListenerManager getDefault() {
		if(DoxyfileListenerManager.instance == null) {
			DoxyfileListenerManager.instance = new DoxyfileListenerManager();
		}
		return DoxyfileListenerManager.instance;
	}
	
	/**
	 * Unregisters a doxyfile listener.
	 * 
	 * @param	listener	a listsner to unregister
	 */
	public void removeDoxyfileListener(DoxyfileListener listener) {
		super.removeListener(listener);
	}
	
	/**
	 * Fires a doxyfile removed event.
	 * 
	 * @param	doxyfile	the removed doxyfile
	 */
	private void fireDoxyfileRemovedEvent(IFile doxyfile) {
		super.fireEvent(new DoxyfileEvent(doxyfile), "doxyfileRemoved");
	}
}
