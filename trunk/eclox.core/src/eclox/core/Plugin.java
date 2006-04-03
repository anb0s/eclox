/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.core;

import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import eclox.core.build.BuildHistory;


/**
 * Implements the core eclox plugin
 * 
 * @author gbrocker
 */
public class Plugin extends org.eclipse.core.runtime.Plugin {
	
	/**
	 * the default plugin instance
	 */
	private static Plugin plugin;
	

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
		BuildHistory.getDefault().load();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		BuildHistory.getDefault().store();
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
	 * Adds the specified message into the plugin's log as a warning.
	 *  
	 * @param message	a string containing a message to log as a warning
	 */
	public void logWarning( String message ) {
	    getLog().log( new Status(Status.WARNING, getBundle().getSymbolicName(), 0, message, null) );
	}
	
	/**
	 * Adds the specified message into the plugin's log as an error.
	 *  
	 * @param message	a string containing a message to log as an error
	 */
	public void logError( String message ) {
	    getLog().log( new Status(Status.ERROR, getBundle().getSymbolicName(), 0, message, null) );
	}
	
	/**
	 * Adds the specified throwable object into the plugin's log as an error.
	 *  
	 * @param throwable	a throwable instance to log
	 */
	public void logError( Throwable throwable ) {
	    getLog().log( new Status(Status.ERROR, getBundle().getSymbolicName(), 0, "Exception caught.", throwable) );
	}
}
