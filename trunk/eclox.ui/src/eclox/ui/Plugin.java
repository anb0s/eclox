package eclox.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import eclox.ui.console.ConsoleManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class Plugin extends AbstractUIPlugin {

	/**
	 * the shared instance
	 */
	private static Plugin plugin;
	
	
	/**
	 * the managed build manager
	 */
	private BuildManager buildManager;
	
	/**
	 * the managed console manager 
	 */
	private ConsoleManager consoleManager;
	
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
		consoleManager = new ConsoleManager();
		buildManager = new BuildManager();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		buildManager = null;
		consoleManager = null;
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
	 * Retrieves the console manager of the plugin.
	 * 
	 * @return the managed console manager instance
	 */
	public ConsoleManager getConsoleManager() {
		return consoleManager; 
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
	 * Show an error with the specified message.
	 * 
	 * @param message	A string containing the message to display.
	 */
	public static void showError( String message ) {
		Shell		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageBox	messageBox = new MessageBox( shell, SWT.ICON_ERROR | SWT.OK );
		
		messageBox.setText( "Eclox" );
		messageBox.setMessage( message );
		messageBox.open();		
	}
	
	/**
	 * Show an error message relative to the specified throwable object.
	 * 
	 * @param	throwable	The object for which an error message must be shown.
	 */
	public static void showError(Throwable throwable) {
		String	message = throwable.getMessage();
		
		if(message != null) {
			showError(message);
		}
		else {
			showError("Caught exception of class "+throwable.getClass().getName()+" with no messages.");
		}
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
