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

package eclox.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eclox.build.BuildHistory;
import eclox.build.BuildJob;
import eclox.ui.plugin.Icons;

/**
 * Implements the plugin class.
 * 
 * @author gbrocker
 */
public class Plugin extends AbstractUIPlugin {
	/**
	 * The default singleton instance.
	 */
	static private Plugin m_defaultInstance = null;
	
	/**
	 * Constructor.
	 */
	public Plugin() {
		super();
		if( m_defaultInstance == null ) {
			m_defaultInstance = this;
		}
	}
	
	/**
	 * Show an error with the specified message.
	 * 
	 * @param message	A string containing the message to display.
	 */
	public void showError( String message ) {
		Shell		shell = getWorkbench().getActiveWorkbenchWindow().getShell();
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
	public void showError(Throwable throwable) {
		String	message = throwable.getMessage();
		
		if(message != null) {
			showError(message);
		}
		else {
			showError("Caught exception of class "+throwable.getClass().getName()+" with no messages.");
		}
	}
	
	/**
	 * 
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		BuildHistory.getDefault().load();		
	}
	
	/**
	 * 
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		BuildJob.getDefault().cancel();
		BuildHistory.getDefault().store();
		super.stop(bundleContext);
	}
	
	/**
	 * Retrieves the default plugin instance.
	 * 
	 * @return	The default plugin instance.
	 */
	static public Plugin getDefault() {
		return m_defaultInstance;
	}
	
	/**
	 * Initializes an image registry with images which are frequently used by the plugin.
	 * 
	 * @param	reg	The image registry to initialize.
	 */
	protected void initializeImageRegistry(ImageRegistry reg) {
		Icons.initializeImageRegistry(reg);
	} 
}
