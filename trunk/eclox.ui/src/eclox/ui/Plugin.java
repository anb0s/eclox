package eclox.ui;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import eclox.ui.console.Console;

/**
 * The main plugin class to be used in the desktop.
 */
public class Plugin extends AbstractUIPlugin {

	/**
	 * the shared instance
	 */
	private static Plugin plugin;
	
	/**
	 * the managed console instance
	 */
	private Console console = new Console();
	
	
	/**
	 * The constructor.
	 */
	public Plugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Plugin getDefault() {
		return plugin;
	}
	
	/**
	 * Retrieves the managed console instance.
	 * 
	 * @return	a console instance
	 */
	public Console getConsole()
	{
		return console;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("eclox.ui", path);
	}
}
