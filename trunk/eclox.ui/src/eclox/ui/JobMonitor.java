/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2008 Guillaume Brocker
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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;

import eclox.core.doxygen.BuildJob;

/**
 * @brief	Monitors all doxygen build jobs and report errors
 * 			concerning doxygen invokation failures.
 * 
 * @author gbrocker
 */
public class JobMonitor extends JobChangeAdapter {

	/**
	 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	public void done(IJobChangeEvent event) {
		
		if( event.getJob().belongsTo(BuildJob.FAMILY) && event.getResult().getCode() == BuildJob.ERROR_DOXYGEN_NOT_FOUND ) {
				Display	display = Plugin.getDefault().getWorkbench().getDisplay();
				
				display.asyncExec(
						new Runnable()
						{
							public void run()
							{
								Plugin.editPreferencesAfterDoxygenInvocationFailed();		
							}
						}
					);
		}
	}

}
