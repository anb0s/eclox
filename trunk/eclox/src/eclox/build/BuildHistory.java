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

package eclox.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import eclox.resource.DoxyfileEvent;
import eclox.resource.DoxyfileListener;
import eclox.resource.DoxyfileListenerManager;
import eclox.ui.Plugin;
import eclox.util.ListenerManager;

/**
 * Implement a build history.
 * 
 * @author gbrocker
 */
public class BuildHistory extends ListenerManager {
	/**
	 * Implement a preference change listener.
	 * 
	 * It is reponsible to resize the history list when the maximal history list
	 * property value changes.
	 *
	 * @author gbrocker
	 */
	private class PreferenceChangedListener implements IPropertyChangeListener {
		/**
		 * Notification that a property has changed.
		 * 
		 * @param	event	the property change event object describing which property changed and how 
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if(event.getProperty() == eclox.preferences.Names.BUILD_HISTORY_SIZE) {
				int	size = ((Integer)event.getNewValue()).intValue();
				
				if(files.size() > size) {
					files.subList(size, files.size()).clear();
					fireBuildHistoryChangedEvent();
				}
			}	
		}
	} 
	
	/**
	 * Implements a listeners for doxyfile removal.
	 */
	private class DoxyfileRemovedListener implements DoxyfileListener {
		/**
		 * @see eclox.resource.DoxyfileListener#doxyfileRemoved(eclox.resource.DoxyfileEvent)
		 */
		public void doxyfileRemoved(DoxyfileEvent event) {
			Iterator it = files.iterator();
			
			while(it.hasNext()) {
				IFile curFile = (IFile)it.next();
				
				if(curFile.getFullPath().equals(event.doxyfile.getFullPath()) == true) {
					files.remove(curFile);
					break;
				}
			}
		}
	}
	
	/**
	 * The current build history.
	 */
	private static BuildHistory currentInstance;
	
	/**
	 * The history list.
	 */
	private List files = new ArrayList();
	
	/**
	 * Retrieve the default build history instance.
	 * 
	 * @return	The default build history instance.
	 */
	public static BuildHistory getDefault() {
		if(BuildHistory.currentInstance == null) {
			BuildHistory.currentInstance = new BuildHistory();
		}
		return BuildHistory.currentInstance;
	}
	
	/**
	 * Constructor.
	 */
	private BuildHistory() {
		super(BuildHistoryListener.class);
		Plugin.getDefault().getPreferenceStore().addPropertyChangeListener(new PreferenceChangedListener());
		DoxyfileListenerManager.getDefault().addDoxyfileListener(new DoxyfileRemovedListener());
	}
	
	/**
	 * Add a new build history listener.
	 * 
	 * @param listener	The build history listener instance to add.
	 */
	public void addBuildHistoryListener(BuildHistoryListener listener) {
		super.addListener(listener);
	}
	
	/**
	 * Loads the history. 
	 */
	public void load() {
		// Retrieves the saved file paths in an array.
		String		savedHistoryContent;			
		String[]	rawPaths;
		savedHistoryContent = Plugin.getDefault().getPluginPreferences().getString( eclox.preferences.Names.BUILD_HISTORY_CONTENT );
		rawPaths = savedHistoryContent.split(":");
		
		// Recreates and reinserts the files in the history.
		IWorkspaceRoot	workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for( int i = rawPaths.length - 1; i >= 0 ; i-- ) {
			IFile currentFile = (IFile) workspaceRoot.findMember( rawPaths[i] );
			if( currentFile != null ) {
				this.log( currentFile );
			}
		}
	}
	
	/**
	 * Log the specified file in the history.
	 * 
	 * @param	file	The file to log in the history.
	 */
	public void log(IFile file) {
		// Purge the existing file.
		if(this.files.contains(file)) {
			this.files.remove(file);
		}
		
		// Make room for the new log entry.
		int size = Plugin.getDefault().getPluginPreferences().getInt(eclox.preferences.Names.BUILD_HISTORY_SIZE);
		if(this.files.size() >= size-1) {
			this.files.subList(size-1, this.files.size()).clear();
		}
		
		// Insert the file in the list and fire somre events.
		this.files.add(0, file);
		this.fireBuildHistoryChangedEvent();
	}
	
	/**
	 * Remove the specified build history listener.
	 * 
	 * @param listener	The build history listener instance to remove.
	 */
	public void removeBuildHistoryListener(BuildHistoryListener listener) {
		super.removeListener(listener);
	}
	
	/**
	 * Retrieve the hostory current size.
	 * 
	 * @return	The history current size
	 */
	public int size() {
		return this.files.size();
	}
	
	/**
	 * Stores the build history.
	 */
	public void store() {
		// Build a string containing the path of all files in the build history.
		String		savedFilePaths = new String();
		Iterator	fileIterator = this.files.iterator();
		while( fileIterator.hasNext() == true ) {
			IFile	currentFile = (IFile) fileIterator.next();
			
			savedFilePaths = savedFilePaths.length() != 0 ? savedFilePaths + ":" : savedFilePaths;
			savedFilePaths = savedFilePaths + currentFile.getFullPath().toString();
		}
		
		// Stores the build history content.
		Plugin.getDefault().getPluginPreferences().setValue(
			eclox.preferences.Names.BUILD_HISTORY_CONTENT,
			savedFilePaths );
	}
	
	/**
	 * Retrieve all file in the build history.
	 * 
	 * @return	An array of the files that have been build.
	 */
	public IFile[] toArray() {
		return (IFile[]) this.files.toArray(new IFile[0]);
	}
	
	/**
	 * Fire a build history changed event to all attached listeners.
	 */
	private void fireBuildHistoryChangedEvent() {
		fireEvent(new BuildHistoryEvent(this), "buildHistoryChanged");
	}
}
