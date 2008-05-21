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

package eclox.ui;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The plugin class.
 */
/**
 * @author gbrocker
 *
 */
public class Plugin extends AbstractUIPlugin {

	private static Plugin	plugin;			///< The singleton instance.
	private BuildManager	buildManager;	///< The managed build manager.
	private JobMonitor		jobMonitor;		///< The managed job monitor.
	
	/**
	 * Asks the user if he wants to edit doxygen configuration after a failed
	 * doxygen invocation.
	 * 
	 * @return	@c true if doxygen configuration has been edited, @c false otherwise
	 */
	public static boolean editPreferencesAfterDoxygenInvocationFailed() {
		Shell	shell = plugin.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		// Asks the user if he wants to edit the preferences to solve the problem.
		boolean	editionWanted = MessageDialog.openQuestion(shell, "Doxygen Not Found", "Eclox was not able to run doxygen. Doxygen is either missing or eclox is not properly configured to use it.\n\nWould you like to edit preferences now ?" );
		if( ! editionWanted ) {
			return false;
		}

		// Allows the user to edit the preferences and eventually launch doxygen again.
		String[]	filter = { eclox.core.ui.PreferencePage.ID };
		int			edited = PreferencesUtil.createPreferenceDialogOn(shell, eclox.core.ui.PreferencePage.ID, filter, null).open();

		return edited == Window.OK;
	}
	
	
	/**
	 * The constructor.
	 */
	public Plugin() {
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		buildManager = new BuildManager();
		buildManager.restoreState();
		
		jobMonitor = new JobMonitor();
		Job.getJobManager().addJobChangeListener(jobMonitor);
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		buildManager.saveState();
		buildManager = null;
		
		Job.getJobManager().removeJobChangeListener(jobMonitor);
		jobMonitor = null;
		
		plugin = null;
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static Plugin getDefault() {
		return plugin;
	}
	
	/**
	 * Retrieves the build manager of the plugin.
	 * 
	 * @return the managed build manager instance
	 */
	public BuildManager getBuildManager() {
		return buildManager; 
	}
	
	/**
	 * Adds the specified throwable object into the plugin's log as an error.
	 *  
	 * @param throwable	a throwable instance to log
	 */
	public static void log( Throwable throwable ) {
	    plugin.getLog().log( new Status(Status.ERROR, plugin.getBundle().getSymbolicName(), 0, "Exception caught. " + throwable.toString(), throwable) );
	}
	
	/**
	 * Adds the specified message into the plugin's log as an error.
	 *  
	 * @param message	a string containing a message to log.
	 */
	public static void log( String message ) {
	    plugin.getLog().log( new Status(Status.ERROR, plugin.getBundle().getSymbolicName(), 0, "Error encountered. " + message, null) );
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin( plugin.getBundle().getSymbolicName(), path);
	}
}
