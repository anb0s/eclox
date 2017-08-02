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
 *     Andre Bossert - Add image registry
 *
 ******************************************************************************/

package eclox.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import eclox.ui.action.MenuItemType;

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
//	public static ImageDescriptor getImageDescriptor(String path) {
//    return AbstractUIPlugin.imageDescriptorFromPlugin( plugin.getBundle().getSymbolicName(), path);
//	}

    public static ImageDescriptor getImageDescriptor(String id) {
        return getDefault().getImageRegistry().getDescriptor(id);
    }

    public static Image getImage(String id) {
        return getDefault().getImageRegistry().get(id);
    }

    protected void initializeImageRegistry(ImageRegistry registry) {
        Bundle bundle = Platform.getBundle(plugin.getBundle().getSymbolicName());
        for(String imageId : MenuItemType.getImageIdsAsList()) {
            String imagePath = "icons/" + imageId + ".gif";
            URL url = bundle.getEntry(imagePath);
            if (url == null) {
                imagePath = "icons/" + imageId + ".png";
            }
            addImageToRegistry(registry, bundle, imagePath, imageId);

        }
        /*
        addImageToRegistry(registry, bundle, "icons/doxyfile.gif", "doxyfile");
        addImageToRegistry(registry, bundle, "icons/eclox.gif", "eclox");
        addImageToRegistry(registry, bundle, "icons/default.png", "default");
        addImageToRegistry(registry, bundle, "icons/eclipse.png", "eclipse");
        addImageToRegistry(registry, bundle, "icons/erase.png", "erase");
        addImageToRegistry(registry, bundle, "icons/explore.png", "explore");
        addImageToRegistry(registry, bundle, "icons/explore1.png", "explore1");
        addImageToRegistry(registry, bundle, "icons/explore2.png", "explore2");
        addImageToRegistry(registry, bundle, "icons/run.png", "run");
        addImageToRegistry(registry, bundle, "icons/user.png", "user");
        */
     }

    protected void addImageToRegistry(ImageRegistry registry, Bundle bundle, String imagePath, String image_id) {
        IPath path = new Path(imagePath);
        URL url = FileLocator.find(bundle, path, null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        registry.put(image_id, desc);
    }

}
