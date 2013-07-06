/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/ 

package eclox.core.doxygen;

/**
 * Defines the interface for objects wanting to receives notifcations
 * about doxygen build jobs.
 * 
 * @author gbrocker
 */
public interface IBuildJobListener {

	/**
	 * Notifies that the job build log has changed.
	 * 
	 * @param	job		the build job that raised the notification
	 * @param	output	a string containing the new output from doxygen
	 */
	void buildJobLogUpdated( BuildJob job, String output );
	
	/**
	 * Notifies that the build job log has been cleared.
	 *  
	 * @param job	the build job that raised the notification
	 */
	void buildJobLogCleared( BuildJob job );
	
	/**
	 * Notifies that the given build job has been removed.
	 * 
	 * @param	job	the build job that raised the notification
	 */
	void buildJobRemoved( BuildJob job );
	
}
