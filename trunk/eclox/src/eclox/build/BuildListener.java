/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003 Guillaume Brocker

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

import java.util.EventListener;

/**
 * Defines the interface for the builder.
 * 
 * @author gbrocker
 */
public interface BuildListener extends EventListener {
	/**
	 * Notify the listener that a build has started
	 * 
	 * @param event	The build event to process.
	 */
	public void buildStarted( BuildEvent event );
	
	/**
	 * Notify the listener that a build has been stopped. The build may not be complet.
	 * 
	 * @param	event	The build event to process.
	 */
	public void buildStopped( BuildEvent event );
	
	/**
	 * Notify the listener the build has ended. This is the normal ending, the build is also complet.
	 * 
	 * @param	event	The event to process.
	 */
	public void buildEnded( BuildEvent event );
			
	/**
	 * Notify the listener that the build process output has changed. A string containing the
	 * new output text is set as the event value.
	 * 
	 * @param	event	The event to process. 
	 */
	public void buildOutputChanged( BuildEvent event );
}
