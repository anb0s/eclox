/**
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

package eclox.core.build;

import java.util.EventObject;

/**
 * Implement the build job output event, reporting that the build job output
 * has been updated.
 * 
 * @author gbrocker
 */
public class BuildOutputEvent extends EventObject {
	/**
	 * The job that raised the event.
	 */
	public BuildJob job;
	
	/**
	 * The text that the job has outputed.
	 */
	public String text;
	
	/**
	 * Constructor
	 * 
	 * @param	job		The build job that raised the event.
	 * @param	text	The text that has been outputed by the build job.
	 */
	public BuildOutputEvent(BuildJob job, String text) {
		super(job);
		this.job = job;
		this.text = text;
	}
}
