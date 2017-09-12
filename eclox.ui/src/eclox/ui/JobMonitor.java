/*******************************************************************************
 * Copyright (C) 2008, 2013, Guillaume Brocker
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

        if (event.getJob().belongsTo(BuildJob.FAMILY)
                && event.getResult().getCode() == BuildJob.ERROR_DOXYGEN_NOT_FOUND) {
            Display display = Plugin.getDefault().getWorkbench().getDisplay();

            display.asyncExec(new Runnable() {
                public void run() {
                    Plugin.editPreferencesAfterDoxygenInvocationFailed();
                }
            });
        }
    }

}
